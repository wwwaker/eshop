package com.example.eshop.controller;

import com.example.eshop.entity.User;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.EmailCodeService;
import com.example.eshop.service.MailService;
import com.example.eshop.service.UserService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    private final UserService userService;
    private final MailService mailService;
    private final CategoryService categoryService;
    private final EmailCodeService emailCodeService;

    public UserController(UserService userService, MailService mailService, CategoryService categoryService, EmailCodeService emailCodeService) {
        this.userService = userService;
        this.mailService = mailService;
        this.categoryService = categoryService;
        this.emailCodeService = emailCodeService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        User user = userService.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "auth/register";
    }

    @PostMapping("/register/send-code")
    @ResponseBody
    public String sendRegisterCode(@RequestParam("email") String email,
                                   HttpSession session) {
        if (email == null || email.trim().isEmpty()) {
            return "{\"success\": false, \"message\": \"邮箱不能为空\"}";
        }

        String normalizedEmail = email.trim();
        String code = emailCodeService.generateCode();
        emailCodeService.saveCode(session, normalizedEmail, code);

        String content = "您的注册验证码是：" + code + "，"
                + emailCodeService.getExpireMinutes() + "分钟内有效。";

        try {
            mailService.sendTextMail(normalizedEmail, "EShop 注册验证码", content);
            return "{\"success\": true, \"message\": \"验证码已发送，请查收邮箱\"}";
        } catch (Exception e) {
            emailCodeService.clearCode(session);
            return "{\"success\": false, \"message\": \"验证码发送失败，请稍后重试\"}";
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("email") String email,
                           @RequestParam("emailCode") String emailCode,
                           @RequestParam("phone") String phone,
                           HttpSession session,
                           Model model) {

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "邮箱不能为空");
            return "auth/register";
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            model.addAttribute("error", "手机号不能为空");
            return "auth/register";
        }
        

        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "用户名已存在");
            return "auth/register";
        }

        if (email == null || email.trim().isEmpty()) {
            model.addAttribute("error", "邮箱不能为空");
            return "auth/register";
        }

        if (emailCode == null || emailCode.trim().isEmpty()) {
            model.addAttribute("error", "请输入邮箱验证码");
            return "auth/register";
        }

        String normalizedEmail = email.trim();
        if (!emailCodeService.verifyCode(session, normalizedEmail, emailCode.trim())) {
            model.addAttribute("error", "邮箱验证码错误或已过期");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(normalizedEmail);
        user.setPhone(phone);

        if (userService.register(user)) {
            emailCodeService.clearCode(session);
            return "redirect:/login";
        } else {
            model.addAttribute("error", "注册失败");
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("categories", categoryService.findAll());
        return "user/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("email") String email,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                HttpSession session,
                                Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        session.setAttribute("user", user);

        if (userService.update(user)) {
            model.addAttribute("user", user);
            model.addAttribute("success", "更新成功");
        } else {
            model.addAttribute("error", "更新失败");
        }
        return "user/profile";
    }

    @GetMapping("/api/user/check-username")
    @ResponseBody
    public UserCheckResponse checkUsernameExists(@RequestParam("username") String username) {
        boolean exists = userService.findByUsername(username) != null;
        return new UserCheckResponse(exists, exists ? "用户名已存在" : "用户名可用");
    }

    @GetMapping("/api/user/check-email")
    @ResponseBody
    public UserCheckResponse checkEmailExists(@RequestParam("email") String email) {
        boolean exists = userService.findByEmail(email) != null;
        return new UserCheckResponse(exists, exists ? "邮箱已存在" : "邮箱可用");
    }

    public static class UserCheckResponse {
        private boolean exists;
        private String message;

        public UserCheckResponse(boolean exists, String message) {
            this.exists = exists;
            this.message = message;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
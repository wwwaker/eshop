package com.example.eshop.controller;

import com.example.eshop.entity.User;
import com.example.eshop.service.UserService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "account/login";
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
            return "account/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "account/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("email") String email,
                           @RequestParam("phone") String phone,
                           Model model) {
        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "用户名已存在");
            return "account/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);

        if (userService.register(user)) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", "注册失败");
            return "account/register";
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
            return "redirect:/account/login";
        }
        model.addAttribute("user", user);
        return "account/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("email") String email,
                                @RequestParam("phone") String phone,
                                @RequestParam("address") String address,
                                HttpSession session,
                                Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/account/login";
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
        return "account/profile";
    }
}

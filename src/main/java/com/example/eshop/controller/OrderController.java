package com.example.eshop.controller;

import com.example.eshop.entity.Order;
import com.example.eshop.entity.User;
import com.example.eshop.service.CartService;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.OrderService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final CartService cartService;
    private final CategoryService categoryService;

    public OrderController(OrderService orderService, CartService cartService, CategoryService categoryService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.categoryService = categoryService;
    }

    @GetMapping("/order/confirm")
    public String confirmOrder(HttpSession session, Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (cartService.getCartCount(user.getId()) == 0) {
            return "redirect:/cart";
        }

        model.addAttribute("user", user);
        model.addAttribute("cartItems", cartService.findByUserId(user.getId()));
        model.addAttribute("total", cartService.getCartTotal(user.getId()));
        model.addAttribute("categories", categoryService.findAll());
        return "order/order-confirm";
    }

    @PostMapping("/order/create")
    public String createOrder(@RequestParam("receiverName") String receiverName,
                              @RequestParam("receiverPhone") String receiverPhone,
                              @RequestParam("receiverAddress") String receiverAddress,
                              HttpSession session,
                              Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Order order = orderService.createOrder(user.getId(), receiverName, receiverPhone, receiverAddress);
            if (order != null) {
                return "redirect:/order/detail?orderNo=" + order.getOrderNo();
            } else {
                model.addAttribute("error", "创建订单失败，购物车为空");
                return "redirect:/cart";
            }
        } catch (Exception e) {//异常的根源在OrderService
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/order/detail")
    public String orderDetail(@RequestParam("orderNo") String orderNo, HttpSession session, Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderService.findByOrderNo(orderNo);
        // 只有管理员或当前订单用户可查看指定订单
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if ((order == null || !order.getUserId().equals(user.getId())) && !isAdmin) {
            return "redirect:/order/list";
        }

        // 管理员查看非自购订单时，禁用支付按钮，且仅可取消未支付订单
        boolean disablePayBtn = isAdmin && !order.getUserId().equals(user.getId());
        model.addAttribute("disablePayBtn", disablePayBtn);
        log.info("{}",disablePayBtn);

        model.addAttribute("order", order);
        model.addAttribute("categories", categoryService.findAll());
        return "order/order-detail";
    }

    @GetMapping("/order/list")
    public String orderList(HttpSession session, Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.findByUserId(user.getId());
        model.addAttribute("orders", orders);
        model.addAttribute("categories", categoryService.findAll());
        return "order/user-order-list";
    }

    @PostMapping("/order/pay")
    public String payOrder(@RequestParam("orderId") Long orderId, HttpSession session) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        orderService.payOrder(orderId);
        return "redirect:/order/list";
    }

    @PostMapping("/order/cancel")
    public String cancelOrder(@RequestParam("orderId") Long orderId, HttpSession session) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        orderService.cancelOrder(orderId);
        return "redirect:/order/list";
    }

}

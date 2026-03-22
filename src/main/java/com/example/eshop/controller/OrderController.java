package com.example.eshop.controller;

import com.example.eshop.entity.Order;
import com.example.eshop.entity.User;
import com.example.eshop.service.CartService;
import com.example.eshop.service.OrderService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

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
        } catch (Exception e) {
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
        if (order == null || !order.getUserId().equals(user.getId())) {
            return "redirect:/order/list";
        }

        model.addAttribute("order", order);
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
        return "order/order-list";
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

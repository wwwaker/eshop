package com.example.eshop.controller;

import com.example.eshop.entity.CartItem;
import com.example.eshop.entity.User;
import com.example.eshop.service.CartService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.findByUserId(user.getId());
        BigDecimal total = cartService.getCartTotal(user.getId());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            HttpSession session,
                            Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (cartService.addToCart(user.getId(), productId, quantity)) {
            model.addAttribute("success", "已添加到购物车");
        } else {
            model.addAttribute("error", "添加失败，库存不足");
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateQuantity(@RequestParam("cartItemId") Long cartItemId,
                                 @RequestParam("quantity") Integer quantity,
                                 HttpSession session) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (quantity > 0) {
            cartService.updateQuantity(cartItemId, quantity);
        } else {
            cartService.removeFromCart(cartItemId);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove")
    public String removeFromCart(@RequestParam("id") Long cartItemId, HttpSession session) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartService.removeFromCart(cartItemId);
        return "redirect:/cart";
    }

    @GetMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        cartService.clearCart(user.getId());
        return "redirect:/cart";
    }
}

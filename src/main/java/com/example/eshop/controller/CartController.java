package com.example.eshop.controller;

import com.example.eshop.dao.ProductDao;
import com.example.eshop.entity.CartItem;
import com.example.eshop.entity.Product;
import com.example.eshop.entity.User;
import com.example.eshop.service.CartService;
import com.example.eshop.service.CategoryService;
import com.example.eshop.util.AuthUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {
    private final CartService cartService;
    private final CategoryService categoryService;

    public CartController(CartService cartService, CategoryService categoryService) {
        this.cartService = cartService;
        this.categoryService = categoryService;
    }

    private boolean checkImageExists(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }
        String imagePath = imageUrl;
        if (imagePath.startsWith("/")) {
            imagePath = imagePath.substring(1);
        }
        Path path = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", imagePath);
        return Files.exists(path);
    }

    private Map<Long, Boolean> buildImageExistsMap(List<CartItem> cartItems) {
        Map<Long, Boolean> imageExistsMap = new HashMap<>();
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                imageExistsMap.put(item.getProduct().getId(), checkImageExists(item.getProduct().getImageUrl()));
            }
        }
        return imageExistsMap;
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.findByUserId(user.getId());
        BigDecimal total = cartService.getCartTotal(user.getId());

        Map<Long, Boolean> imageExistsMap = buildImageExistsMap(cartItems);

        Map<Long, Boolean> stockStatusMap = new HashMap<>();
        boolean allStockSufficient = true;

        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                boolean sufficient = item.getQuantity() <= item.getProduct().getStock();
                stockStatusMap.put(item.getProduct().getId(), sufficient);
                if (!sufficient) {
                    allStockSufficient = false;
                }
            }
        }


        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("imageExistsMap", imageExistsMap);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("stockStatusMap", stockStatusMap);
        model.addAttribute("allStockSufficient", allStockSufficient);
        return "cart/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User user = AuthUtil.getCurrentUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        if (cartService.addToCart(user.getId(), productId, quantity)) {
            redirectAttributes.addFlashAttribute("success", "已添加到购物车");
        } else {
            redirectAttributes.addFlashAttribute("error", "添加失败，库存不足");
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
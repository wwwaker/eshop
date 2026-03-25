package com.example.eshop.controller;

import com.example.eshop.entity.Category;
import com.example.eshop.entity.Order;
import com.example.eshop.entity.Product;
import com.example.eshop.entity.User;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.OrderService;
import com.example.eshop.service.ProductService;
import com.example.eshop.service.UserService;
import org.apache.ibatis.ognl.internal.entry.CacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private final Map<String, CacheEntry> dashboardCache = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION_MS = 60000;

    @GetMapping("")
    public String adminIndex(Model model) {
        loadDashboardData(model);
        return "admin/index";
    }

    private void loadDashboardData(Model model) {
        String cacheKey = "dashboard";
        CacheEntry cachedData = dashboardCache.get(cacheKey);
        
        if (cachedData != null && !cachedData.isExpired()) {
            Map<String, Object> data = cachedData.getData();
            model.addAttribute("totalProducts", data.get("totalProducts"));
            model.addAttribute("totalCategories", data.get("totalCategories"));
            model.addAttribute("totalOrders", data.get("totalOrders"));
            model.addAttribute("totalUsers", data.get("totalUsers"));
            model.addAttribute("todayNewProducts", data.get("todayNewProducts"));
            model.addAttribute("todayOrders", data.get("todayOrders"));
            model.addAttribute("todayNewUsers", data.get("todayNewUsers"));
            model.addAttribute("todaySales", data.get("todaySales"));
            model.addAttribute("salesTrend", data.get("salesTrend"));
            model.addAttribute("hotProducts", data.get("hotProducts"));
            model.addAttribute("userActivity", data.get("userActivity"));
            return;
        }

        int totalProducts = productService.findAll().size();
        int totalCategories = categoryService.findAll().size();
        int totalOrders = orderService.findAll().size();
        int totalUsers = userService.findAll().size();

        int todayNewProducts = productService.countTodayNewProducts();
        int todayOrders = orderService.countTodayOrders();
        int todayNewUsers = orderService.countTodayNewUsers();

        Map<String, Object> todaySalesData = orderService.getTodaySales();
        BigDecimal todaySales = new BigDecimal(todaySalesData.get("todayTotalAmount").toString());

        List<Map<String, Object>> salesTrend = productService.getSalesTrend(7);
        List<Map<String, Object>> hotProducts = productService.getHotProducts(10);
        Map<String, Object> userActivity = orderService.getUserActivityStats();

        Map<String, Object> data = new HashMap<>();
        data.put("totalProducts", totalProducts);
        data.put("totalCategories", totalCategories);
        data.put("totalOrders", totalOrders);
        data.put("totalUsers", totalUsers);
        data.put("todayNewProducts", todayNewProducts);
        data.put("todayOrders", todayOrders);
        data.put("todayNewUsers", todayNewUsers);
        data.put("todaySales", todaySales);
        data.put("salesTrend", salesTrend);
        data.put("hotProducts", hotProducts);
        data.put("userActivity", userActivity);

        dashboardCache.put(cacheKey, new CacheEntry(data, CACHE_DURATION_MS));

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);

        model.addAttribute("todayNewProducts", todayNewProducts);
        model.addAttribute("todayOrders", todayOrders);
        model.addAttribute("todayNewUsers", todayNewUsers);
        model.addAttribute("todaySales", todaySales);

        model.addAttribute("salesTrend", salesTrend);
        model.addAttribute("hotProducts", hotProducts);
        model.addAttribute("userActivity", userActivity);
    }

    @GetMapping("/products")
    public String adminProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/product/add")
    public String addProductPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    @PostMapping("/product/save")
    public String saveProduct(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam("price") BigDecimal price,
                              @RequestParam("stock") Integer stock,
                              @RequestParam("categoryId") Long categoryId,
                              @RequestParam("imageUrl") String imageUrl,
                              @RequestParam("status") String status) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategoryId(categoryId);
        product.setImageUrl(imageUrl);
        product.setStatus(status);

        productService.save(product);
        dashboardCache.clear();
        return "redirect:/admin/products";
    }

    @GetMapping("/product/edit")
    public String editProductPage(@RequestParam("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product-form";
    }

    @GetMapping("/product/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        productService.deleteById(id);
        dashboardCache.clear();
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String adminOrders(Model model) {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "admin/orders";
    }

    @PostMapping("/order/ship")
    public String shipOrder(@RequestParam("orderId") Long orderId) {
        orderService.shipOrder(orderId);
        dashboardCache.clear();
        return "redirect:/admin/orders";
    }

    @GetMapping("/categories")
    public String adminCategories(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @PostMapping("/category/save")
    public String saveCategory(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam("name") String name,
                               @RequestParam("description") String description,
                               @RequestParam("sortOrder") Integer sortOrder) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(sortOrder);

        categoryService.save(category);
        dashboardCache.clear();
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete")
    public String deleteCategory(@RequestParam("id") Long id) {
        categoryService.deleteById(id);
        dashboardCache.clear();
        return "redirect:/admin/categories";
    }

    @GetMapping("/users")
    public String adminUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        dashboardCache.clear();
        return "redirect:/admin/users";
    }

    private static class CacheEntry {
        private final Map<String, Object> data;
        private final long expirationTime;

        public CacheEntry(Map<String, Object> data, long duration) {
            this.data = data;
            this.expirationTime = System.currentTimeMillis() + duration;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}

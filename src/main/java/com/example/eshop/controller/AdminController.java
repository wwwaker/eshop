package com.example.eshop.controller;

import com.example.eshop.entity.Category;
import com.example.eshop.entity.Order;
import com.example.eshop.entity.Product;
import com.example.eshop.entity.User;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.OrderService;
import com.example.eshop.service.ProductService;
import com.example.eshop.service.UserService;
import com.example.eshop.util.AuthUtil;
import com.example.eshop.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.ognl.internal.entry.CacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final UserService userService;

    private static final int PAGE_SIZE = 10;

    public AdminController(ProductService productService, CategoryService categoryService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.userService = userService;
    }

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
    public String adminProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "id_desc") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        List<Product> products = productService.findFiltered(keyword, categoryId, status, sort, page, PAGE_SIZE);
        int totalItems = productService.countFiltered(keyword, categoryId, status);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
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
    public String adminOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "created_desc") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        List<Order> orders = orderService.findFiltered(keyword, status, sort, page, PAGE_SIZE);
        int totalItems = orderService.countFiltered(keyword, status);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        model.addAttribute("orders", orders);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        return "admin/orders";
    }

    @GetMapping("/order/detail")
    public String orderDetail(@RequestParam("id") Long id, Model model) {
        Order order = orderService.findById(id);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        return "admin/order-detail";
    }

    @PostMapping("/order/ship")
    public String shipOrder(@RequestParam("orderId") Long orderId) {
        orderService.shipOrder(orderId);
        dashboardCache.clear();
        return "redirect:/admin/orders";
    }

    @PostMapping("/order/complete")
    public String completeOrder(@RequestParam("orderId") Long orderId) {
        orderService.completeOrder(orderId);
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

    @GetMapping("/category/edit")
    public String editCategoryPage(@RequestParam("id") Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/category-form";
    }

    @GetMapping("/category/products")
    public String categoryProducts(@RequestParam("id") Long categoryId, Model model) {
        Category category = categoryService.findById(categoryId);
        List<Product> products = productService.findByCategoryId(categoryId);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        return "admin/category-products";
    }

    @PostMapping("/category/moveProducts")
    public String moveProducts(@RequestParam("fromCategoryId") Long fromCategoryId,
                               @RequestParam("toCategoryId") Long toCategoryId,
                               Model model) {
        if (fromCategoryId.equals(toCategoryId)) {
            model.addAttribute("error", "源分类和目标分类不能相同");
            return "redirect:/admin/category/products?id=" + fromCategoryId;
        }
        productService.moveToCategory(fromCategoryId, toCategoryId);
        dashboardCache.clear();
        return "redirect:/admin/category/products?id=" + toCategoryId;
    }

    @GetMapping("/category/delete")
    public String deleteCategory(@RequestParam("id") Long id) {
        categoryService.deleteById(id);
        dashboardCache.clear();
        return "redirect:/admin/categories";
    }

    @GetMapping("/users")
    public String adminUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        List<User> users = userService.findFiltered(keyword, role, page, PAGE_SIZE);
        int totalItems = userService.countFiltered(keyword, role);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        return "admin/users";
    }

    @PostMapping("/user/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        dashboardCache.clear();
        return "redirect:/admin/users";
    }

    @GetMapping("/user/edit")
    public String editUserPage(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "redirect:/admin/users";
        }

        List<Order> orders = orderService.findByUserId(id);
        int totalOrders = orders.size();
        BigDecimal totalSpent = orders.stream()
                .filter(o -> "PAID".equals(o.getStatus()) || "SHIPPED".equals(o.getStatus()) || "COMPLETED".equals(o.getStatus()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSpent", totalSpent);
        return "admin/user-form";
    }

    @PostMapping("/user/save")
    public String saveUser(@RequestParam(value = "id", required = false) Long id,
                           @RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam(value = "phone", required = false) String phone,
                           @RequestParam(value = "address", required = false) String address,
                           @RequestParam("role") String role,
                           @RequestParam(value = "password", required = false) String password,
                           Model model) {

        User existingUser = userService.findByUsername(username);
        if (existingUser != null && (id == null || !existingUser.getId().equals(id))) {
            model.addAttribute("error", "用户名已存在");
            User user = userService.findById(id);
            model.addAttribute("user", user);
            model.addAttribute("orders", orderService.findByUserId(id));
            return "admin/user-form";
        }

        User user = userService.findById(id);
        if (user == null) {
            model.addAttribute("error", "用户不存在");
            return "admin/user-form";
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);

        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordUtil.encrypt(password));
        }

        userService.update(user);
        dashboardCache.clear();
        return "redirect:/admin/users";
    }

    @GetMapping("/user/orders")
    public String viewUserOrders(@RequestParam("id") Long userId, HttpSession session, Model model) {
        User currentUser = AuthUtil.getCurrentUser(session);
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/login";
        }

        User targetUser = userService.findById(userId);
        if (targetUser == null) {
            model.addAttribute("error", "用户不存在");
            return "redirect:/admin/users";
        }

        List<Order> orders = orderService.findByUserId(userId);
        model.addAttribute("orders", orders);
        model.addAttribute("viewingUser", targetUser);
        return "admin/user-order-list";
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

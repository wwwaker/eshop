package com.example.eshop.controller;

import com.example.eshop.entity.Category;
import com.example.eshop.entity.Order;
import com.example.eshop.entity.Product;
import com.example.eshop.entity.User;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.OrderService;
import com.example.eshop.service.ProductService;
import com.example.eshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("")
    public String adminIndex(Model model) {
        model.addAttribute("productCount", productService.findAll().size());
        model.addAttribute("categoryCount", categoryService.findAll().size());
        model.addAttribute("orderCount", orderService.findAll().size());
        model.addAttribute("userCount", userService.findAll().size());
        return "admin/index";
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
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete")
    public String deleteCategory(@RequestParam("id") Long id) {
        categoryService.deleteById(id);
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
        return "redirect:/admin/users";
    }
}

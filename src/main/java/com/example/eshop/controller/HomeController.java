package com.example.eshop.controller;

import com.example.eshop.entity.Category;
import com.example.eshop.entity.Product;
import com.example.eshop.service.CategoryService;
import com.example.eshop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        List<Product> products = productService.findAllOnSale();
        List<Category> categories = categoryService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword);
        List<Category> categories = categoryService.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @GetMapping("/category")
    public String category(@RequestParam("id") Long categoryId, Model model) {
        List<Product> products = productService.findByCategoryId(categoryId);
        List<Category> categories = categoryService.findAll();
        Category currentCategory = categoryService.findById(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", currentCategory);
        return "index";
    }

    @GetMapping("/product")
    public String productDetail(@RequestParam("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/";
        }
        model.addAttribute("product", product);
        return "product-detail";
    }
}

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
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // 辅助方法：检查单个商品图片是否存在
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
    
    // 辅助方法：为商品列表生成图片存在性Map
    private Map<Long, Boolean> buildImageExistsMap(List<Product> products) {
        Map<Long, Boolean> imageExistsMap = new HashMap<>();
        for (Product product : products) {
            imageExistsMap.put(product.getId(), checkImageExists(product.getImageUrl()));
        }
        return imageExistsMap;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        List<Product> products = productService.findAllOnSale();
        List<Category> categories = categoryService.findAll();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("imageExistsMap", buildImageExistsMap(products));
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Product> products = productService.searchByName(keyword);
        List<Category> categories = categoryService.findAll();
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("imageExistsMap", buildImageExistsMap(products));
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
        model.addAttribute("imageExistsMap", buildImageExistsMap(products));
        return "index";
    }

    @GetMapping("/product")
    public String productDetail(@RequestParam("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/";
        }
        
        // 检查图片文件是否存在
        boolean imageExists = checkImageExists(product.getImageUrl());
        
        model.addAttribute("product", product);
        model.addAttribute("imageExists", imageExists);
        model.addAttribute("categories", categoryService.findAll());
        return "product/product-detail";
    }
    @GetMapping("/api/search-suggestions")
    @ResponseBody
    public List<String> searchSuggestions(@RequestParam("keyword") String keyword) {
        return productService.searchSuggestions(keyword);
    }
}

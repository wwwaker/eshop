package com.example.eshop.service;

import com.example.eshop.entity.Product;
import com.example.eshop.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Product findById(Long id) {
        return productDao.findById(id);
    }

    public List<Product> findAll() {
        return productDao.findAll();
    }

    public List<Product> findAllOnSale() {
        return productDao.findAllOnSale();
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return productDao.findByCategoryId(categoryId);
    }

    public List<Product> searchByName(String keyword) {
        return productDao.searchByName(keyword);
    }

    public List<String> searchSuggestions(String keyword) {
        return productDao.searchSuggestions(keyword);
    }

    public boolean save(Product product) {
        if (product.getId() == null) {
            return productDao.insert(product) > 0;
        } else {
            return productDao.update(product) > 0;
        }
    }

    public boolean deleteById(Long id) {
        return productDao.deleteById(id) > 0;
    }

    public boolean decreaseStock(Long id, Integer quantity) {
        return productDao.decreaseStock(id, quantity) > 0;
    }

}

package com.example.eshop.service;

import com.example.eshop.entity.Product;
import com.example.eshop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public Product findById(Long id) {
        return productMapper.findById(id);
    }

    public List<Product> findAll() {
        return productMapper.findAll();
    }

    public List<Product> findAllOnSale() {
        return productMapper.findAllOnSale();
    }

    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    public List<Product> searchByName(String keyword) {
        return productMapper.searchByName(keyword);
    }

    public boolean save(Product product) {
        if (product.getId() == null) {
            return productMapper.insert(product) > 0;
        } else {
            return productMapper.update(product) > 0;
        }
    }

    public boolean deleteById(Long id) {
        return productMapper.deleteById(id) > 0;
    }

    public boolean decreaseStock(Long id, Integer quantity) {
        return productMapper.decreaseStock(id, quantity) > 0;
    }

}

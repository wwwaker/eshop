package com.example.eshop.service;

import com.example.eshop.entity.Category;
import com.example.eshop.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }

    public List<Category> findAll() {
        return categoryMapper.findAll();
    }

    public boolean save(Category category) {
        if (category.getId() == null) {
            return categoryMapper.insert(category) > 0;
        } else {
            return categoryMapper.update(category) > 0;
        }
    }

    public boolean deleteById(Long id) {
        return categoryMapper.deleteById(id) > 0;
    }
}

package com.example.eshop.service;

import com.example.eshop.entity.Category;
import com.example.eshop.dao.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    public Category findById(Long id) {
        return categoryDao.findById(id);
    }

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    public boolean save(Category category) {
        if (category.getId() == null) {
            return categoryDao.insert(category) > 0;
        } else {
            return categoryDao.update(category) > 0;
        }
    }

    public boolean deleteById(Long id) {
        return categoryDao.deleteById(id) > 0;
    }
}

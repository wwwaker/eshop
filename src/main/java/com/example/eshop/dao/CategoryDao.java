package com.example.eshop.dao;

import com.example.eshop.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryDao {

    Category findById(Long id);

    List<Category> findAll();

    int insert(Category category);

    int update(Category category);

    int deleteById(Long id);
}

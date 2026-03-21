package com.example.eshop.mapper;

import com.example.eshop.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category findById(Long id);

    @Select("SELECT * FROM categories ORDER BY sort_order")
    List<Category> findAll();

    @Insert("INSERT INTO categories (name, description, sort_order) " +
            "VALUES (#{name}, #{description}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE categories SET name = #{name}, description = #{description}, " +
            "sort_order = #{sortOrder} WHERE id = #{id}")
    int update(Category category);

    @Delete("DELETE FROM categories WHERE id = #{id}")
    int deleteById(Long id);
}

package com.example.eshop.dao;

import com.example.eshop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductDao {

    Product findById(Long id);

    List<Product> findAllOnSale();

    List<Product> findAll();

    List<Product> findByCategoryId(Long categoryId);

    List<Product> searchByName(String keyword);

    int insert(Product product);

    int update(Product product);

    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int deleteById(Long id);
}

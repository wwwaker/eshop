package com.example.eshop.dao;

import com.example.eshop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductDao {

    Product findById(Long id);

    List<Product> findAllOnSale();

    List<Product> findAll();

    List<Product> findByCategoryId(Long categoryId);

    List<Product> searchByName(String keyword);

    List<String> searchSuggestions(String keyword);

    int insert(Product product);

    int update(Product product);

    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    int deleteById(Long id);
    
    int countTodayNewProducts();
    
    List<Map<String, Object>> getSalesTrend(@Param("days") int days);
    
    List<Map<String, Object>> getHotProducts(@Param("limit") int limit);

    int moveToCategory(@Param("fromCategoryId") Long fromCategoryId, @Param("toCategoryId") Long toCategoryId);

    List<Product> findFiltered(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("status") String status, @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

    int countFiltered(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("status") String status);

}

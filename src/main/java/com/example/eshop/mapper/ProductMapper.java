package com.example.eshop.mapper;

import com.example.eshop.entity.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = #{id}")
    Product findById(Long id);

    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id WHERE p.status = 'ON_SALE' ORDER BY p.id DESC")
    List<Product> findAllOnSale();

    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.id DESC")
    List<Product> findAll();

    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.category_id = #{categoryId} AND p.status = 'ON_SALE'")
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT p.*, c.name as category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.name LIKE CONCAT('%', #{keyword}, '%') AND p.status = 'ON_SALE'")
    List<Product> searchByName(String keyword);

    @Insert("INSERT INTO products (name, description, price, stock, category_id, image_url, status) " +
            "VALUES (#{name}, #{description}, #{price}, #{stock}, #{categoryId}, #{imageUrl}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE products SET name = #{name}, description = #{description}, price = #{price}, " +
            "stock = #{stock}, category_id = #{categoryId}, image_url = #{imageUrl}, status = #{status} " +
            "WHERE id = #{id}")
    int update(Product product);

    @Update("UPDATE products SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(Long id);
}

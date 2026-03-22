package com.example.eshop.mapper;

import com.example.eshop.entity.CartItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartItemMapper {

    @Select("SELECT ci.*, p.id as product_id, p.name, p.price, p.image_url, p.stock FROM cart_items ci " +
            "JOIN products p ON ci.product_id = p.id WHERE ci.user_id = #{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "product.id", column = "product_id"),
            @Result(property = "product.name", column = "name"),
            @Result(property = "product.price", column = "price"),
            @Result(property = "product.imageUrl", column = "image_url"),
            @Result(property = "product.stock", column = "stock")
    })
    List<CartItem> findByUserId(Long userId);

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId}")
    CartItem findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Insert("INSERT INTO cart_items (user_id, product_id, quantity) VALUES (#{userId}, #{productId}, #{quantity})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem cartItem);

    @Update("UPDATE cart_items SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(CartItem cartItem);

    @Delete("DELETE FROM cart_items WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM cart_items WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM cart_items WHERE user_id = #{userId}")
    int countByUserId(Long userId);
}

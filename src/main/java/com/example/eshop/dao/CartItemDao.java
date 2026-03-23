package com.example.eshop.dao;

import com.example.eshop.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemDao {

    List<CartItem> findByUserId(Long userId);

    CartItem findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    int insert(CartItem cartItem);

    int updateQuantity(CartItem cartItem);

    int deleteById(Long id);

    int deleteByUserId(Long userId);

    int countByUserId(Long userId);
}

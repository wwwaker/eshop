package com.example.eshop.service;

import com.example.eshop.entity.CartItem;
import com.example.eshop.entity.Product;
import com.example.eshop.mapper.CartItemMapper;
import com.example.eshop.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    public List<CartItem> findByUserId(Long userId) {
        List<CartItem> items = cartItemMapper.findByUserId(userId);
        // 确保每个CartItem都有Product信息
        for (CartItem item : items) {
            if (item.getProduct() == null) {
                item.setProduct(productMapper.findById(item.getProductId()));
            }
        }
        return items;
    }

    @Transactional
    public boolean addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productMapper.findById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        CartItem existingItem = cartItemMapper.findByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemMapper.updateQuantity(existingItem) > 0;
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            return cartItemMapper.insert(cartItem) > 0;
        }
    }

    public boolean updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(quantity);
        return cartItemMapper.updateQuantity(cartItem) > 0;
    }

    public boolean removeFromCart(Long cartItemId) {
        return cartItemMapper.deleteById(cartItemId) > 0;
    }

    public boolean clearCart(Long userId) {
        return cartItemMapper.deleteByUserId(userId) > 0;
    }

    public int getCartCount(Long userId) {
        return cartItemMapper.countByUserId(userId);
    }

    public BigDecimal getCartTotal(Long userId) {
        List<CartItem> items = cartItemMapper.findByUserId(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}

package com.example.eshop.service;

import com.example.eshop.entity.CartItem;
import com.example.eshop.entity.Product;
import com.example.eshop.dao.CartItemDao;
import com.example.eshop.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final CartItemDao cartItemDao;
    private final ProductDao productDao;

    public CartService(CartItemDao cartItemDao, ProductDao productDao) {
        this.cartItemDao = cartItemDao;
        this.productDao = productDao;
    }

    public List<CartItem> findByUserId(Long userId) {
        List<CartItem> items = cartItemDao.findByUserId(userId);
        // 确保每个CartItem都有Product信息
        for (CartItem item : items) {
            if (item.getProduct() == null) {
                item.setProduct(productDao.findById(item.getProductId()));
            }
        }
        return items;
    }

    @Transactional
    public boolean addToCart(Long userId, Long productId, Integer quantity) {
        Product product = productDao.findById(productId);
        //检验库存
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        // 如果购物车中已经存在该商品，则更新数量
        CartItem existingItem = cartItemDao.findByUserIdAndProductId(userId, productId);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartItemDao.updateQuantity(existingItem) > 0;
        } else {
            // 否则插入新的
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            return cartItemDao.insert(cartItem) > 0;
        }
    }

    public boolean updateQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(quantity);
        return cartItemDao.updateQuantity(cartItem) > 0;
    }

    public boolean removeFromCart(Long cartItemId) {
        return cartItemDao.deleteById(cartItemId) > 0;
    }

    public boolean clearCart(Long userId) {
        return cartItemDao.deleteByUserId(userId) > 0;
    }

    public int getCartCount(Long userId) {
        return cartItemDao.countByUserId(userId);
    }

    public BigDecimal getCartTotal(Long userId) {
        List<CartItem> items = cartItemDao.findByUserId(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }

}

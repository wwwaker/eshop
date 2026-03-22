package com.example.eshop.mapper;

import com.example.eshop.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Select("SELECT oi.*, p.image_url FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = #{orderId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "productName", column = "product_name"),
            @Result(property = "productPrice", column = "product_price"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "subtotal", column = "subtotal"),
            @Result(property = "product.imageUrl", column = "image_url")
    })
    List<OrderItem> findByOrderId(Long orderId);

    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_price, quantity, subtotal) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productPrice}, #{quantity}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem orderItem);

    @Insert("<script>" +
            "INSERT INTO order_items (order_id, product_id, product_name, product_price, quantity, subtotal) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.orderId}, #{item.productId}, #{item.productName}, #{item.productPrice}, #{item.quantity}, #{item.subtotal})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<OrderItem> items);
}

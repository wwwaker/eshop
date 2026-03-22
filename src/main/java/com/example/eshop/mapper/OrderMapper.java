package com.example.eshop.mapper;

import com.example.eshop.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderNo", column = "order_no"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "receiverName", column = "receiver_name"),
            @Result(property = "receiverPhone", column = "receiver_phone"),
            @Result(property = "receiverAddress", column = "receiver_address"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "user.username", column = "username")
    })
    Order findById(Long id);

    @Select("SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id WHERE o.order_no = #{orderNo}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderNo", column = "order_no"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "receiverName", column = "receiver_name"),
            @Result(property = "receiverPhone", column = "receiver_phone"),
            @Result(property = "receiverAddress", column = "receiver_address"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "user.username", column = "username")
    })
    Order findByOrderNo(String orderNo);

    @Select("SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id " +
            "WHERE o.user_id = #{userId} ORDER BY o.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderNo", column = "order_no"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "receiverName", column = "receiver_name"),
            @Result(property = "receiverPhone", column = "receiver_phone"),
            @Result(property = "receiverAddress", column = "receiver_address"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "user.username", column = "username")
    })
    List<Order> findByUserId(Long userId);

    @Select("SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.created_at DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderNo", column = "order_no"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "receiverName", column = "receiver_name"),
            @Result(property = "receiverPhone", column = "receiver_phone"),
            @Result(property = "receiverAddress", column = "receiver_address"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at"),
            @Result(property = "user.username", column = "username")
    })
    List<Order> findAll();

    @Insert("INSERT INTO orders (order_no, user_id, total_amount, status, receiver_name, receiver_phone, receiver_address) " +
            "VALUES (#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{receiverName}, #{receiverPhone}, #{receiverAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);
}

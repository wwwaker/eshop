package com.example.eshop.dao;

import com.example.eshop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDao {

    Order findById(Long id);

    Order findByOrderNo(String orderNo);

    List<Order> findByUserId(Long userId);

    List<Order> findAll();

    int insert(Order order);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    int deleteById(Long id);
}

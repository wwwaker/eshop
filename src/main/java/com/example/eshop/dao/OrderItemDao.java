package com.example.eshop.dao;

import com.example.eshop.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemDao {

    List<OrderItem> findByOrderId(Long orderId);

    int insert(OrderItem orderItem);

    int batchInsert(List<OrderItem> items);
}

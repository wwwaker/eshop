package com.example.eshop.service;

import com.example.eshop.entity.CartItem;
import com.example.eshop.entity.Order;
import com.example.eshop.entity.OrderItem;
import com.example.eshop.dao.CartItemDao;
import com.example.eshop.dao.OrderItemDao;
import com.example.eshop.dao.OrderDao;
import com.example.eshop.dao.ProductDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private CartItemDao cartItemDao;

    @Autowired
    private ProductDao productDao;

    public Order findById(Long id) {
        Order order = orderDao.findById(id);
        if (order != null) {
            order.setItems(orderItemDao.findByOrderId(id));
        }
        return order;
    }

    public Order findByOrderNo(String orderNo) {
        Order order = orderDao.findByOrderNo(orderNo);
        if (order != null) {
            order.setItems(orderItemDao.findByOrderId(order.getId()));
        }
        return order;
    }

    public List<Order> findByUserId(Long userId) {
        List<Order> orders = orderDao.findByUserId(userId);
        for (Order order : orders) {
            order.setItems(orderItemDao.findByOrderId(order.getId()));
        }
        return orders;
    }

    public List<Order> findAll() {
        List<Order> orders = orderDao.findAll();
        for (Order order : orders) {
            order.setItems(orderItemDao.findByOrderId(order.getId()));
        }
        return orders;
    }

    @Transactional
    public Order createOrder(Long userId, String receiverName, String receiverPhone, String receiverAddress) {
        System.out.println("开始创建订单，用户 ID: " + userId);
        List<CartItem> cartItems = cartItemDao.findByUserId(userId);
        System.out.println("购物车项目数量：" + cartItems.size());
        if (cartItems.isEmpty()) {
            System.out.println("购物车为空");
            return null;
        }

        for (CartItem item : cartItems) {
            if (item.getProduct() == null) {
                System.out.println("商品信息缺失，正在加载商品 ID: " + item.getProductId());
                item.setProduct(productDao.findById(item.getProductId()));
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            System.out.println("购物车项目：商品 ID=" + item.getProductId() + ", 数量=" + item.getQuantity());
            if (item.getProduct() == null) {
                System.out.println("商品为 null，商品 ID: " + item.getProductId());
                throw new RuntimeException("商品不存在");
            }
            if (item.getProduct().getStock() < item.getQuantity()) {
                System.out.println("商品库存不足：" + item.getProduct().getName() + ", 库存=" + item.getProduct().getStock() + ", 数量=" + item.getQuantity());
                throw new RuntimeException("商品 " + item.getProduct().getName() + " 库存不足");
            }
            totalAmount = totalAmount.add(item.getSubtotal());
            System.out.println("商品小计：" + item.getSubtotal() + ", 累计总金额：" + totalAmount);
        }

        Order order = new Order();
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        System.out.println("创建订单：订单号=" + orderNo + ", 总金额=" + totalAmount);
        int insertResult = orderDao.insert(order);
        System.out.println("保存订单结果：" + insertResult + ", 订单 ID: " + order.getId());

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setProductPrice(cartItem.getProduct().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getSubtotal());
            System.out.println("创建订单项：商品 ID=" + cartItem.getProductId() + ", 数量=" + cartItem.getQuantity() + ", 小计=" + cartItem.getSubtotal());
            int itemInsertResult = orderItemDao.insert(orderItem);
            System.out.println("保存订单项结果：" + itemInsertResult);

            int stockResult = productDao.decreaseStock(cartItem.getProductId(), cartItem.getQuantity());
            System.out.println("减少库存结果：" + stockResult + ", 商品 ID=" + cartItem.getProductId() + ", 数量=" + cartItem.getQuantity());
        }

        int deleteResult = cartItemDao.deleteByUserId(userId);
        System.out.println("清空购物车结果：" + deleteResult);

        System.out.println("订单创建完成，订单号：" + orderNo);
        return order;
    }

    public boolean payOrder(Long orderId) {
        return orderDao.updateStatus(orderId, "PAID") > 0;
    }

    public boolean shipOrder(Long orderId) {
        return orderDao.updateStatus(orderId, "SHIPPED") > 0;
    }

    public boolean completeOrder(Long orderId) {
        return orderDao.updateStatus(orderId, "COMPLETED") > 0;
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        // 🔍 获取订单信息
        Order order = orderDao.findById(orderId);
        if (order == null) {
            return false;
        }
        
        // 🔍 获取订单项列表
        List<OrderItem> orderItems = orderItemDao.findByOrderId(orderId);
        
        // 🔍 将商品重新添加到购物车
        for (OrderItem item : orderItems) {
            CartItem existingItem = cartItemDao.findByUserIdAndProductId(order.getUserId(), item.getProductId());
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                cartItemDao.updateQuantity(existingItem);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setUserId(order.getUserId());
                cartItem.setProductId(item.getProductId());
                cartItem.setQuantity(item.getQuantity());
                cartItemDao.insert(cartItem);
            }
            
            // 🔍 恢复库存
            productDao.increaseStock(item.getProductId(), item.getQuantity());
        }
        
        // 更新订单状态为已取消
        return orderDao.updateStatus(orderId, "CANCELLED") > 0;
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD" + date + random;
    }

    public int countTodayOrders() {
        return orderDao.countTodayOrders();
    }

    public Map<String, Object> getTodaySales() {
        return orderDao.getTodaySales();
    }

    public int countTodayNewUsers() {
        return orderDao.countTodayNewUsers();
    }

    public Map<String, Object> getUserActivityStats() {
        return orderDao.getUserActivityStats();
    }
}

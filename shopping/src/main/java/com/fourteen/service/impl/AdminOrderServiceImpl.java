package com.fourteen.service.impl;

import com.fourteen.mapper.OrderMapper;
import com.fourteen.mapper.UserAddressMapper;
import com.fourteen.mapper.UserMapper;
import com.fourteen.pojo.*;
import com.fourteen.service.AdminOrderService;
import com.fourteen.service.EmailService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private EmailService emailService;

    @Override
    public PageResult<Map<String, Object>> getOrderList(Integer page, Integer pageSize, Integer status) {
        PageHelper.startPage(page, pageSize);
        
        List<Order> orders;
        if (status != null && status > 0) {
            // 查询所有用户的订单（需要修改Mapper支持）
            orders = orderMapper.findAllByStatus(status);
        } else {
            orders = orderMapper.findAll();
        }
        
        Page<Order> p = (Page<Order>) orders;
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getOrderId());
            orderMap.put("userId", order.getUserId());
            
            // 获取用户信息
            User user = userMapper.findByUserId(order.getUserId());
            if (user != null) {
                orderMap.put("username", user.getUsername());
                orderMap.put("email", user.getEmail());
            }
            
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("createdAt", order.getCreatedAt());
            orderMap.put("updatedAt", order.getUpdatedAt());
            
            // 获取收货地址
            UserAddress address = userAddressMapper.findById(order.getAddressId(), order.getUserId());
            orderMap.put("address", address);
            
            // 获取订单商品
            List<OrderItem> items = orderMapper.findItemsByOrderId(order.getOrderId());
            orderMap.put("items", items);
            
            resultList.add(orderMap);
        }
        
        return new PageResult<>(p.getTotal(), resultList);
    }

    @Override
    public Result getOrderDetail(Integer orderId) {
        // 查找订单（不限制用户）
        Order order = orderMapper.findByOrderId(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("userId", order.getUserId());
        data.put("totalAmount", order.getTotalAmount());
        data.put("status", order.getStatus());
        data.put("addressId", order.getAddressId());
        data.put("remark", order.getRemark());
        data.put("createdAt", order.getCreatedAt());
        data.put("updatedAt", order.getUpdatedAt());
        
        // 获取用户信息
        User user = userMapper.findByUserId(order.getUserId());
        data.put("user", user);
        
        // 获取收货地址
        UserAddress address = userAddressMapper.findById(order.getAddressId(), order.getUserId());
        data.put("address", address);
        
        // 获取订单商品
        List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
        data.put("items", items);
        
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result shipOrder(Integer orderId, String trackingNumber, String shippingCompany) {
        Order order = orderMapper.findByOrderId(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 只有已支付的订单可以发货
        if (order.getStatus() != 2) {
            return Result.error("订单状态不正确，无法发货");
        }
        
        // 更新订单状态为已发货
        orderMapper.updateStatus(orderId, 3); // 3: 已发货
        
        log.info("订单发货成功：orderId={}, trackingNumber={}, shippingCompany={}", 
                orderId, trackingNumber, shippingCompany);
        
        // 发送发货通知邮件
        try {
            User user = userMapper.findByUserId(order.getUserId());
            if (user != null && user.getEmail() != null) {
                emailService.sendShippingNotificationEmail(user.getEmail(), orderId, trackingNumber);
            }
        } catch (Exception e) {
            log.error("发送发货通知邮件失败", e);
            // 邮件发送失败不影响发货操作
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("status", 3);
        data.put("trackingNumber", trackingNumber);
        data.put("shippingCompany", shippingCompany);
        
        return Result.success(data);
    }
}


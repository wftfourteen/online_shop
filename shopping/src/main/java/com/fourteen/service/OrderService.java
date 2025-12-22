package com.fourteen.service;

import com.fourteen.pojo.Result;

import java.util.Map;

public interface OrderService {
    Result createOrder(Integer userId, Map<String, Object> orderRequest);
    
    Result getOrderList(Integer userId, Integer status);
    
    Result getOrderDetail(Integer userId, Integer orderId);
    
    Result cancelOrder(Integer userId, Integer orderId);
    
    Result confirmReceipt(Integer userId, Integer orderId);
}


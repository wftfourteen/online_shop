package com.fourteen.service;

import com.fourteen.pojo.Result;

import java.util.Map;

public interface PaymentService {
    Result payOrder(Integer userId, Integer orderId, String paymentMethod);
    
    Result getPaymentStatus(Integer userId, Integer orderId);
}


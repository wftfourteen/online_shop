package com.fourteen.service;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Result;

import java.util.Map;

public interface AdminOrderService {
    PageResult<Map<String, Object>> getOrderList(Integer page, Integer pageSize, Integer status);
    
    Result getOrderDetail(Integer orderId);
    
    Result shipOrder(Integer orderId, String trackingNumber, String shippingCompany);
}


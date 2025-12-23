package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.OrderService;
import com.fourteen.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public Result createOrder(HttpServletRequest request, @RequestBody Map<String, Object> orderRequest) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("创建订单：userId={}", userId);
        return orderService.createOrder(userId, orderRequest);
    }

    /**
     * 创建订单并立即支付（合并接口）
     * 用于快速购买流程，点击购买后直接完成支付，无需跳转支付页面
     */
    @PostMapping("/create-and-pay")
    public Result createOrderAndPay(HttpServletRequest request, @RequestBody Map<String, Object> orderRequest) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("创建订单并支付：userId={}", userId);
        
        try {
            // 创建订单
            Result createResult = orderService.createOrder(userId, orderRequest);
            if (createResult.getCode() == 0) {
                return createResult; // 创建失败，直接返回
            }
            
            // 获取订单ID
            @SuppressWarnings("unchecked")
            Map<String, Object> orderData = (Map<String, Object>) createResult.getData();
            Integer orderId = (Integer) orderData.get("orderId");
            
            // 获取支付方式（可选，默认支付宝）
            String paymentMethod = (String) orderRequest.get("paymentMethod");
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                paymentMethod = "alipay"; // 默认支付方式
            }
            
            // 立即支付（模拟支付成功）
            Result payResult = paymentService.payOrder(userId, orderId, paymentMethod);
            
            if (payResult.getCode() == 1) {
                log.info("订单创建并支付成功：orderId={}, userId={}", orderId, userId);
            }
            
            return payResult;
            
        } catch (Exception e) {
            log.error("创建订单并支付失败：userId={}", userId, e);
            return Result.error("创建订单并支付失败，请稍后重试");
        }
    }

    @GetMapping
    public Result getOrderList(HttpServletRequest request, 
                               @RequestParam(required = false) Integer status) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取订单列表：userId={}, status={}", userId, status);
        return orderService.getOrderList(userId, status);
    }

    @GetMapping("/{orderId}")
    public Result getOrderDetail(HttpServletRequest request, @PathVariable Integer orderId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取订单详情：userId={}, orderId={}", userId, orderId);
        return orderService.getOrderDetail(userId, orderId);
    }

    @PutMapping("/{orderId}/cancel")
    public Result cancelOrder(HttpServletRequest request, @PathVariable Integer orderId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("取消订单：userId={}, orderId={}", userId, orderId);
        return orderService.cancelOrder(userId, orderId);
    }

    @PutMapping("/{orderId}/confirm")
    public Result confirmReceipt(HttpServletRequest request, @PathVariable Integer orderId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("确认收货：userId={}, orderId={}", userId, orderId);
        return orderService.confirmReceipt(userId, orderId);
    }
}


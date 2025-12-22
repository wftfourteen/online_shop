package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.OrderService;
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

    @PostMapping
    public Result createOrder(HttpServletRequest request, @RequestBody Map<String, Object> orderRequest) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("创建订单：userId={}", userId);
        return orderService.createOrder(userId, orderRequest);
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


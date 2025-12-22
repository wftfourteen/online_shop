package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public Result payOrder(HttpServletRequest request, @RequestBody Map<String, Object> paymentRequest) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer orderId = (Integer) paymentRequest.get("orderId");
        String paymentMethod = (String) paymentRequest.get("paymentMethod"); // alipay, wechat
        
        log.info("订单支付：userId={}, orderId={}, paymentMethod={}", userId, orderId, paymentMethod);
        
        if (orderId == null) {
            return Result.error("订单ID不能为空");
        }
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            return Result.error("请选择支付方式");
        }
        
        return paymentService.payOrder(userId, orderId, paymentMethod);
    }

    @GetMapping("/status/{orderId}")
    public Result getPaymentStatus(HttpServletRequest request, @PathVariable Integer orderId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("查询支付状态：userId={}, orderId={}", userId, orderId);
        return paymentService.getPaymentStatus(userId, orderId);
    }
}


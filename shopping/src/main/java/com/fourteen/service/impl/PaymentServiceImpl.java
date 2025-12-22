package com.fourteen.service.impl;

import com.fourteen.mapper.OrderMapper;
import com.fourteen.mapper.UserMapper;
import com.fourteen.pojo.Order;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;
import com.fourteen.service.EmailService;
import com.fourteen.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Result payOrder(Integer userId, Integer orderId, String paymentMethod) {
        Order order = orderMapper.findById(orderId, userId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 只有待支付订单可以支付
        if (order.getStatus() != 1) {
            return Result.error("订单状态不正确，无法支付");
        }
        
        // 模拟支付处理（实际应该调用第三方支付接口）
        // 这里简单模拟支付成功
        try {
            // 模拟支付延迟
            Thread.sleep(500);
            
            // 更新订单状态为已支付
            orderMapper.updateStatus(orderId, 2); // 2: 已支付
            
            log.info("订单支付成功：orderId={}, userId={}, paymentMethod={}, amount={}", 
                    orderId, userId, paymentMethod, order.getTotalAmount());
            
            // 发送支付成功邮件通知
            try {
                User user = userMapper.findByUserId(userId);
                if (user != null && user.getEmail() != null) {
                    emailService.sendOrderConfirmationEmail(user.getEmail(), orderId, order.getTotalAmount());
                }
            } catch (Exception e) {
                log.error("发送支付成功邮件失败", e);
                // 邮件发送失败不影响支付结果
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("status", 2);
            data.put("paymentMethod", paymentMethod);
            data.put("amount", order.getTotalAmount());
            
            return Result.success(data);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.error("支付处理失败");
        } catch (Exception e) {
            log.error("支付失败", e);
            return Result.error("支付失败：" + e.getMessage());
        }
    }

    @Override
    public Result getPaymentStatus(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId, userId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("status", order.getStatus());
        data.put("totalAmount", order.getTotalAmount());
        
        return Result.success(data);
    }
}


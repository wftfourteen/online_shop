package com.fourteen.service;

public interface EmailService {
    void sendOrderConfirmationEmail(String toEmail, Integer orderId, Double totalAmount);
    
    /**
     * 发送支付成功邮件
     */
    void sendPaymentSuccessEmail(String toEmail, Integer orderId, Double totalAmount, String paymentMethod);
    
    void sendShippingNotificationEmail(String toEmail, Integer orderId, String trackingNumber);
    
    void sendRefundNotificationEmail(String toEmail, Integer orderId, Double refundAmount);
}


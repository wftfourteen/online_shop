package com.fourteen.service;

public interface EmailService {
    void sendOrderConfirmationEmail(String toEmail, Integer orderId, Double totalAmount);
    
    void sendShippingNotificationEmail(String toEmail, Integer orderId, String trackingNumber);
    
    void sendRefundNotificationEmail(String toEmail, Integer orderId, Double refundAmount);
}


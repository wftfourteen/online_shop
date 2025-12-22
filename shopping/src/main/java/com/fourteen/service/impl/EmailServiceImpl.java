package com.fourteen.service.impl;

import com.fourteen.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.from:noreply@shopping.com}")
    private String fromEmail;

    @Override
    public void sendOrderConfirmationEmail(String toEmail, Integer orderId, Double totalAmount) {
        if (mailSender == null) {
            log.warn("邮件服务未配置，跳过发送订单确认邮件：orderId={}, toEmail={}", orderId, toEmail);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("订单确认 - 订单号：" + orderId);
            message.setText(String.format(
                "尊敬的客户，\n\n" +
                "您的订单已成功创建！\n\n" +
                "订单号：%d\n" +
                "订单金额：¥%.2f\n\n" +
                "我们将尽快为您处理订单。\n\n" +
                "感谢您的购买！\n\n" +
                "此致\n" +
                "购物网站团队",
                orderId, totalAmount
            ));
            
            mailSender.send(message);
            log.info("订单确认邮件发送成功：orderId={}, toEmail={}", orderId, toEmail);
        } catch (Exception e) {
            log.error("发送订单确认邮件失败：orderId={}, toEmail={}", orderId, toEmail, e);
        }
    }

    @Override
    public void sendShippingNotificationEmail(String toEmail, Integer orderId, String trackingNumber) {
        if (mailSender == null) {
            log.warn("邮件服务未配置，跳过发送发货通知邮件：orderId={}, toEmail={}", orderId, toEmail);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("订单已发货 - 订单号：" + orderId);
            message.setText(String.format(
                "尊敬的客户，\n\n" +
                "您的订单已发货！\n\n" +
                "订单号：%d\n" +
                "物流单号：%s\n\n" +
                "请保持手机畅通，以便快递员联系您。\n\n" +
                "感谢您的购买！\n\n" +
                "此致\n" +
                "购物网站团队",
                orderId, trackingNumber != null ? trackingNumber : "待更新"
            ));
            
            mailSender.send(message);
            log.info("发货通知邮件发送成功：orderId={}, toEmail={}", orderId, toEmail);
        } catch (Exception e) {
            log.error("发送发货通知邮件失败：orderId={}, toEmail={}", orderId, toEmail, e);
        }
    }

    @Override
    public void sendRefundNotificationEmail(String toEmail, Integer orderId, Double refundAmount) {
        if (mailSender == null) {
            log.warn("邮件服务未配置，跳过发送退款通知邮件：orderId={}, toEmail={}", orderId, toEmail);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("退款通知 - 订单号：" + orderId);
            message.setText(String.format(
                "尊敬的客户，\n\n" +
                "您的退款申请已处理完成。\n\n" +
                "订单号：%d\n" +
                "退款金额：¥%.2f\n\n" +
                "退款将在1-3个工作日内退回到您的原支付账户。\n\n" +
                "如有疑问，请联系客服。\n\n" +
                "此致\n" +
                "购物网站团队",
                orderId, refundAmount
            ));
            
            mailSender.send(message);
            log.info("退款通知邮件发送成功：orderId={}, toEmail={}", orderId, toEmail);
        } catch (Exception e) {
            log.error("发送退款通知邮件失败：orderId={}, toEmail={}", orderId, toEmail, e);
        }
    }
}


package com.fourteen.service.impl;

import com.fourteen.mapper.*;
import com.fourteen.pojo.*;
import com.fourteen.service.EmailService;
import com.fourteen.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    
    @Autowired
    private ProductsMapper productsMapper;
    
    @Autowired
    private UserAddressMapper userAddressMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Result createOrder(Integer userId, Map<String, Object> orderRequest) {
        try {
            Integer addressId = (Integer) orderRequest.get("addressId");
            String remark = (String) orderRequest.get("remark");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderRequest.get("items");
            
            // 验证收货地址
            if (addressId == null) {
                return Result.error("请选择收货地址");
            }
            UserAddress address = userAddressMapper.findById(addressId, userId);
            if (address == null) {
                return Result.error("收货地址不存在");
            }
            
            // 验证购物车商品
            if (items == null || items.isEmpty()) {
                return Result.error("请选择要购买的商品");
            }
            
            double totalAmount = 0;
            List<OrderItem> orderItems = new ArrayList<>();
            
            // 处理每个商品
            for (Map<String, Object> item : items) {
                Integer productId = (Integer) item.get("productId");
                Integer quantity = (Integer) item.get("quantity");
                
                Product product = productsMapper.findById(productId);
                if (product == null) {
                    return Result.error("商品不存在");
                }
                if (product.getStatus() != 1) {
                    return Result.error("商品 " + product.getName() + " 已下架");
                }
                if (quantity > product.getStock()) {
                    return Result.error("商品 " + product.getName() + " 库存不足");
                }
                
                double subtotal = product.getPrice() * quantity;
                totalAmount += subtotal;
                
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(productId);
                orderItem.setProductName(product.getName());
                orderItem.setProductImage(product.getMainImage());
                orderItem.setPrice(product.getPrice());
                orderItem.setQuantity(quantity);
                orderItem.setSubtotal(subtotal);
                orderItems.add(orderItem);
            }
            
            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setTotalAmount(totalAmount);
            order.setStatus(1); // 待支付
            order.setAddressId(addressId);
            order.setRemark(remark);
            orderMapper.addOrder(order);
            
            // 添加订单商品
            for (OrderItem item : orderItems) {
                item.setOrderId(order.getOrderId());
                orderMapper.addOrderItem(item);
                
                // 减少库存（实际应该用乐观锁或悲观锁）
                Product product = productsMapper.findById(item.getProductId());
                productsMapper.updateStock(item.getProductId(), product.getStock() - item.getQuantity());
            }
            
            // 清空购物车（可选，根据需求决定）
            // shoppingCartMapper.deleteByUserId(userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getOrderId());
            data.put("totalAmount", order.getTotalAmount());
            
            // 发送订单确认邮件
            try {
                User user = userMapper.findByUserId(userId);
                if (user != null && user.getEmail() != null) {
                    emailService.sendOrderConfirmationEmail(user.getEmail(), order.getOrderId(), order.getTotalAmount());
                }
            } catch (Exception e) {
                log.error("发送订单确认邮件失败", e);
                // 邮件发送失败不影响订单创建
            }
            
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("创建订单失败", e);
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }

    @Override
    public Result getOrderList(Integer userId, Integer status) {
        List<Order> orders;
        if (status != null && status > 0) {
            orders = orderMapper.findByUserIdAndStatus(userId, status);
        } else {
            orders = orderMapper.findByUserId(userId);
        }
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("orderId", order.getOrderId());
            orderMap.put("totalAmount", order.getTotalAmount());
            orderMap.put("status", order.getStatus());
            orderMap.put("createdAt", order.getCreatedAt());
            orderMap.put("remark", order.getRemark());
            
            // 获取订单商品
            List<OrderItem> items = orderMapper.findItemsByOrderId(order.getOrderId());
            orderMap.put("items", items);
            
            resultList.add(orderMap);
        }
        
        return Result.success(resultList);
    }

    @Override
    public Result getOrderDetail(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId, userId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("totalAmount", order.getTotalAmount());
        data.put("status", order.getStatus());
        data.put("addressId", order.getAddressId());
        data.put("remark", order.getRemark());
        data.put("createdAt", order.getCreatedAt());
        data.put("updatedAt", order.getUpdatedAt());
        
        // 获取收货地址
        UserAddress address = userAddressMapper.findById(order.getAddressId(), userId);
        data.put("address", address);
        
        // 获取订单商品
        List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
        data.put("items", items);
        
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result cancelOrder(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId, userId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 只有待支付和已支付的订单可以取消
        if (order.getStatus() != 1 && order.getStatus() != 2) {
            return Result.error("当前订单状态不允许取消");
        }
        
        // 更新订单状态为已取消
        orderMapper.updateStatus(orderId, 5);
        
        // 恢复库存
        List<OrderItem> items = orderMapper.findItemsByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productsMapper.findById(item.getProductId());
            if (product != null) {
                productsMapper.updateStock(item.getProductId(), product.getStock() + item.getQuantity());
            }
        }
        
        return Result.success();
    }

    @Override
    @Transactional
    public Result confirmReceipt(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId, userId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        // 只有已发货的订单可以确认收货
        if (order.getStatus() != 3) {
            return Result.error("订单状态不正确，无法确认收货");
        }
        
        // 更新订单状态为已完成
        orderMapper.updateStatus(orderId, 4); // 4: 已完成
        
        log.info("订单确认收货：orderId={}, userId={}", orderId, userId);
        
        return Result.success("确认收货成功");
    }
}


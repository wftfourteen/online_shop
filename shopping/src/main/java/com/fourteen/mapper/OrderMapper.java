package com.fourteen.mapper;

import com.fourteen.pojo.Order;
import com.fourteen.pojo.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {
    
    @Insert("insert into orders (user_id, total_amount, status, address_id, remark, created_at, updated_at) " +
            "values (#{userId}, #{totalAmount}, #{status}, #{addressId}, #{remark}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "orderId")
    void addOrder(Order order);
    
    @Select("select order_id, user_id, total_amount, status, address_id, remark, created_at, updated_at " +
            "from orders where order_id=#{orderId} and user_id=#{userId}")
    Order findById(Integer orderId, Integer userId);
    
    @Select("select order_id, user_id, total_amount, status, address_id, remark, created_at, updated_at " +
            "from orders where user_id=#{userId} " +
            "order by created_at desc")
    List<Order> findByUserId(Integer userId);
    
    @Select("select order_id, user_id, total_amount, status, address_id, remark, created_at, updated_at " +
            "from orders where user_id=#{userId} and status=#{status} " +
            "order by created_at desc")
    List<Order> findByUserIdAndStatus(Integer userId, Integer status);
    
    @Update("update orders set status=#{status}, updated_at=CURRENT_TIMESTAMP " +
            "where order_id=#{orderId}")
    void updateStatus(Integer orderId, Integer status);
    
    @Insert("insert into order_items (order_id, product_id, product_name, product_image, price, quantity, subtotal) " +
            "values (#{orderId}, #{productId}, #{productName}, #{productImage}, #{price}, #{quantity}, #{subtotal})")
    void addOrderItem(OrderItem orderItem);
    
    @Select("select item_id, order_id, product_id, product_name, product_image, price, quantity, subtotal " +
            "from order_items where order_id=#{orderId}")
    List<OrderItem> findItemsByOrderId(Integer orderId);
    
    @Select("select order_id, user_id, total_amount, status, address_id, remark, created_at, updated_at " +
            "from orders order by created_at desc")
    List<Order> findAll();
    
    @Select("select order_id, user_id, total_amount, status, address_id, remark, created_at, updated_at " +
            "from orders where status=#{status} order by created_at desc")
    List<Order> findAllByStatus(Integer status);
    
    @Select("select order_id, user_id, total_amount, status, address_id, remark, created_at, updated_at " +
            "from orders where order_id=#{orderId}")
    Order findByOrderId(Integer orderId);
}


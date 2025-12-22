package com.fourteen.mapper;

import com.fourteen.pojo.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    
    @Select("select cart_id, user_id, product_id, quantity, created_at " +
            "from shopping_carts where user_id=#{userId}")
    List<ShoppingCart> findByUserId(Integer userId);
    
    @Select("select cart_id, user_id, product_id, quantity, created_at " +
            "from shopping_carts where user_id=#{userId} and product_id=#{productId}")
    ShoppingCart findByUserIdAndProductId(Integer userId, Integer productId);
    
    @Insert("insert into shopping_carts (user_id, product_id, quantity, created_at) " +
            "values (#{userId}, #{productId}, #{quantity}, CURRENT_TIMESTAMP)")
    void addCartItem(ShoppingCart cart);
    
    @Update("update shopping_carts set quantity=#{quantity} " +
            "where user_id=#{userId} and product_id=#{productId}")
    void updateQuantity(Integer userId, Integer productId, Integer quantity);
    
    @Delete("delete from shopping_carts where cart_id=#{cartId} and user_id=#{userId}")
    void deleteById(Integer cartId, Integer userId);
    
    @Delete("delete from shopping_carts where user_id=#{userId}")
    void deleteByUserId(Integer userId);
}


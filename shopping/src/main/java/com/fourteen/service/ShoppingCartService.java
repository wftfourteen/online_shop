package com.fourteen.service;

import com.fourteen.pojo.Result;

import java.util.Map;

public interface ShoppingCartService {
    Result addToCart(Integer userId, Integer productId, Integer quantity);
    
    Result getCartList(Integer userId);
    
    Result updateCartItem(Integer userId, Integer cartId, Integer quantity);
    
    Result deleteCartItem(Integer userId, Integer cartId);
}


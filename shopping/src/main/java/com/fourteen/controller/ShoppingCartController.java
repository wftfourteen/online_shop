package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/cart")
public class ShoppingCartController {
    
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping
    public Result addToCart(HttpServletRequest request, @RequestBody Map<String, Integer> requestBody) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer productId = requestBody.get("productId");
        Integer quantity = requestBody.get("quantity");
        
        log.info("添加商品到购物车：userId={}, productId={}, quantity={}", userId, productId, quantity);
        
        if (productId == null || quantity == null || quantity < 1) {
            return Result.error("参数错误");
        }
        
        return shoppingCartService.addToCart(userId, productId, quantity);
    }

    @GetMapping
    public Result getCartList(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取购物车列表：userId={}", userId);
        return shoppingCartService.getCartList(userId);
    }

    @PutMapping("/{cartId}")
    public Result updateCartItem(HttpServletRequest request, 
                                 @PathVariable Integer cartId,
                                 @RequestBody Map<String, Integer> requestBody) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer quantity = requestBody.get("quantity");
        
        log.info("更新购物车项：userId={}, cartId={}, quantity={}", userId, cartId, quantity);
        
        if (quantity == null || quantity < 1) {
            return Result.error("数量必须大于0");
        }
        
        return shoppingCartService.updateCartItem(userId, cartId, quantity);
    }

    @DeleteMapping("/{cartId}")
    public Result deleteCartItem(HttpServletRequest request, @PathVariable Integer cartId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("删除购物车项：userId={}, cartId={}", userId, cartId);
        return shoppingCartService.deleteCartItem(userId, cartId);
    }
}


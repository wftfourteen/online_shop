package com.fourteen.service.impl;

import com.fourteen.mapper.ProductsMapper;
import com.fourteen.mapper.ShoppingCartMapper;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.ShoppingCart;
import com.fourteen.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    
    @Autowired
    private ProductsMapper productsMapper;

    @Override
    public Result addToCart(Integer userId, Integer productId, Integer quantity) {
        // 验证商品是否存在且上架
        Product product = productsMapper.findById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (product.getStatus() != 1) {
            return Result.error("该商品已下架，无法购买");
        }
        
        // 验证库存
        if (quantity > product.getStock()) {
            return Result.error("库存不足，最大可购买 " + product.getStock() + " 件");
        }
        
        // 检查购物车中是否已有该商品
        ShoppingCart existingCart = shoppingCartMapper.findByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            // 更新数量
            int newQuantity = existingCart.getQuantity() + quantity;
            if (newQuantity > product.getStock()) {
                return Result.error("库存不足，最大可购买 " + product.getStock() + " 件");
            }
            shoppingCartMapper.updateQuantity(userId, productId, newQuantity);
        } else {
            // 新增购物车项
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            shoppingCartMapper.addCartItem(cart);
        }
        
        return Result.success();
    }

    @Override
    public Result getCartList(Integer userId) {
        List<ShoppingCart> cartList = shoppingCartMapper.findByUserId(userId);
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (ShoppingCart cart : cartList) {
            Product product = productsMapper.findById(cart.getProductId());
            if (product == null) {
                continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("cartId", cart.getCartId());
            item.put("productId", product.getProductId());
            item.put("productName", product.getName());
            item.put("mainImage", product.getMainImage());
            item.put("price", product.getPrice());
            item.put("quantity", cart.getQuantity());
            item.put("subtotal", product.getPrice() * cart.getQuantity());
            item.put("stock", product.getStock());
            resultList.add(item);
        }
        
        return Result.success(resultList);
    }

    @Override
    public Result updateCartItem(Integer userId, Integer cartId, Integer quantity) {
        // 先查找购物车项
        List<ShoppingCart> cartList = shoppingCartMapper.findByUserId(userId);
        ShoppingCart cart = null;
        for (ShoppingCart c : cartList) {
            if (c.getCartId().equals(cartId)) {
                cart = c;
                break;
            }
        }
        
        if (cart == null) {
            return Result.error("购物车项不存在");
        }
        
        // 验证商品库存
        Product product = productsMapper.findById(cart.getProductId());
        if (product == null) {
            return Result.error("商品不存在");
        }
        if (quantity > product.getStock()) {
            return Result.error("库存不足，最大可购买 " + product.getStock() + " 件");
        }
        
        shoppingCartMapper.updateQuantity(userId, cart.getProductId(), quantity);
        return Result.success();
    }

    @Override
    public Result deleteCartItem(Integer userId, Integer cartId) {
        shoppingCartMapper.deleteById(cartId, userId);
        return Result.success();
    }
}


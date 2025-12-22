package com.fourteen.controller;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.pojo.Result;
import com.fourteen.service.AdminProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/products")
public class AdminProductController {
    
    @Autowired
    private AdminProductService adminProductService;

    @PostMapping
    public Result addProduct(HttpServletRequest request, @RequestBody Product product) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("添加商品：userId={}", userId);
        return adminProductService.addProduct(product);
    }

    @PutMapping("/{productId}")
    public Result updateProduct(HttpServletRequest request, 
                                @PathVariable Integer productId,
                                @RequestBody Product product) {
        Integer userId = (Integer) request.getAttribute("userId");
        product.setProductId(productId);
        log.info("更新商品：userId={}, productId={}", userId, productId);
        return adminProductService.updateProduct(product);
    }

    @DeleteMapping("/{productId}")
    public Result deleteProduct(HttpServletRequest request, @PathVariable Integer productId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("删除商品：userId={}, productId={}", userId, productId);
        return adminProductService.deleteProduct(productId);
    }

    @GetMapping
    public Result getProductList(HttpServletRequest request, ProductsQueryParam param) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取商品列表：userId={}", userId);
        PageResult<Product> pageResult = adminProductService.getProductList(param);
        
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("list", pageResult.getRows());
        data.put("total", pageResult.getTotal());
        data.put("totalPage", (int) Math.ceil((double) pageResult.getTotal() / param.getPageSize()));
        data.put("currentPage", param.getPage());
        
        return Result.success(data);
    }

    @GetMapping("/{productId}")
    public Result getProductById(HttpServletRequest request, @PathVariable Integer productId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取商品详情：userId={}, productId={}", userId, productId);
        return adminProductService.getProductById(productId);
    }

    @PutMapping("/{productId}/status")
    public Result updateProductStatus(HttpServletRequest request,
                                      @PathVariable Integer productId,
                                      @RequestBody Map<String, Integer> requestBody) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer status = requestBody.get("status");
        log.info("更新商品状态：userId={}, productId={}, status={}", userId, productId, status);
        return adminProductService.updateProductStatus(productId, status);
    }

    @PutMapping("/{productId}/stock")
    public Result updateProductStock(HttpServletRequest request,
                                     @PathVariable Integer productId,
                                     @RequestBody Map<String, Integer> requestBody) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer stock = requestBody.get("stock");
        log.info("更新商品库存：userId={}, productId={}, stock={}", userId, productId, stock);
        return adminProductService.updateProductStock(productId, stock);
    }
}


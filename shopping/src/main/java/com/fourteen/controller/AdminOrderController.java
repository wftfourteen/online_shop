package com.fourteen.controller;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Result;
import com.fourteen.service.AdminOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/admin/orders")
public class AdminOrderController {
    
    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    public Result getOrderList(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer pageSize,
                                @RequestParam(required = false) Integer status) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取订单列表：userId={}, page={}, pageSize={}, status={}", userId, page, pageSize, status);
        
        PageResult<Map<String, Object>> pageResult = adminOrderService.getOrderList(page, pageSize, status);
        
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("list", pageResult.getRows());
        data.put("total", pageResult.getTotal());
        data.put("totalPage", (int) Math.ceil((double) pageResult.getTotal() / pageSize));
        data.put("currentPage", page);
        
        return Result.success(data);
    }

    @GetMapping("/{orderId}")
    public Result getOrderDetail(HttpServletRequest request, @PathVariable Integer orderId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取订单详情：userId={}, orderId={}", userId, orderId);
        return adminOrderService.getOrderDetail(orderId);
    }

    @PutMapping("/{orderId}/ship")
    public Result shipOrder(HttpServletRequest request,
                            @PathVariable Integer orderId,
                            @RequestBody Map<String, String> shipRequest) {
        Integer userId = (Integer) request.getAttribute("userId");
        String trackingNumber = shipRequest.get("trackingNumber");
        String shippingCompany = shipRequest.get("shippingCompany");
        
        log.info("订单发货：userId={}, orderId={}, trackingNumber={}, shippingCompany={}", 
                userId, orderId, trackingNumber, shippingCompany);
        
        return adminOrderService.shipOrder(orderId, trackingNumber, shippingCompany);
    }
}


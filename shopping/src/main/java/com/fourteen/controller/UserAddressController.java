package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.pojo.UserAddress;
import com.fourteen.service.UserAddressService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/addresses")
public class UserAddressController {
    
    @Autowired
    private UserAddressService userAddressService;

    @PostMapping
    public Result addAddress(HttpServletRequest request, @RequestBody UserAddress address) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("添加收货地址：userId={}", userId);
        return userAddressService.addAddress(userId, address);
    }

    @GetMapping
    public Result getAddressList(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("获取收货地址列表：userId={}", userId);
        return userAddressService.getAddressList(userId);
    }

    @PutMapping("/{addressId}")
    public Result updateAddress(HttpServletRequest request, 
                                @PathVariable Integer addressId,
                                @RequestBody UserAddress address) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("更新收货地址：userId={}, addressId={}", userId, addressId);
        return userAddressService.updateAddress(userId, addressId, address);
    }

    @DeleteMapping("/{addressId}")
    public Result deleteAddress(HttpServletRequest request, @PathVariable Integer addressId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("删除收货地址：userId={}, addressId={}", userId, addressId);
        return userAddressService.deleteAddress(userId, addressId);
    }
}


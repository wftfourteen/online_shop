package com.fourteen.service.impl;

import com.fourteen.mapper.UserAddressMapper;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.UserAddress;
import com.fourteen.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {
    
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public Result addAddress(Integer userId, UserAddress address) {
        address.setUserId(userId);
        userAddressMapper.addAddress(address);
        return Result.success();
    }

    @Override
    public Result getAddressList(Integer userId) {
        List<UserAddress> addressList = userAddressMapper.findByUserId(userId);
        return Result.success(addressList);
    }

    @Override
    public Result updateAddress(Integer userId, Integer addressId, UserAddress address) {
        UserAddress existingAddress = userAddressMapper.findById(addressId, userId);
        if (existingAddress == null) {
            return Result.error("地址不存在");
        }
        
        address.setAddressId(addressId);
        address.setUserId(userId);
        userAddressMapper.updateAddress(address);
        return Result.success();
    }

    @Override
    public Result deleteAddress(Integer userId, Integer addressId) {
        UserAddress existingAddress = userAddressMapper.findById(addressId, userId);
        if (existingAddress == null) {
            return Result.error("地址不存在");
        }
        
        userAddressMapper.deleteAddress(addressId, userId);
        return Result.success();
    }
}


package com.fourteen.service;

import com.fourteen.pojo.Result;
import com.fourteen.pojo.UserAddress;

public interface UserAddressService {
    Result addAddress(Integer userId, UserAddress address);
    
    Result getAddressList(Integer userId);
    
    Result updateAddress(Integer userId, Integer addressId, UserAddress address);
    
    Result deleteAddress(Integer userId, Integer addressId);
}


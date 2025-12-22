package com.fourteen.mapper;

import com.fourteen.pojo.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserAddressMapper {
    
    @Select("select address_id, user_id, receiver_name, receiver_phone, province, city, " +
            "district, detail_address, created_at " +
            "from user_addresses where user_id=#{userId} " +
            "order by created_at desc")
    List<UserAddress> findByUserId(Integer userId);
    
    @Select("select address_id, user_id, receiver_name, receiver_phone, province, city, " +
            "district, detail_address, created_at " +
            "from user_addresses where address_id=#{addressId} and user_id=#{userId}")
    UserAddress findById(Integer addressId, Integer userId);
    
    @Insert("insert into user_addresses (user_id, receiver_name, receiver_phone, province, " +
            "city, district, detail_address, created_at) " +
            "values (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, " +
            "#{city}, #{district}, #{detailAddress}, CURRENT_TIMESTAMP)")
    void addAddress(UserAddress address);
    
    @Update("update user_addresses set receiver_name=#{receiverName}, " +
            "receiver_phone=#{receiverPhone}, province=#{province}, city=#{city}, " +
            "district=#{district}, detail_address=#{detailAddress} " +
            "where address_id=#{addressId} and user_id=#{userId}")
    void updateAddress(UserAddress address);
    
    @Delete("delete from user_addresses where address_id=#{addressId} and user_id=#{userId}")
    void deleteAddress(Integer addressId, Integer userId);
}


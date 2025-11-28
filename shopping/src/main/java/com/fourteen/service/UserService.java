package com.fourteen.service;

import com.fourteen.pojo.LoginInfo;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;

public interface UserService {
    Result addUser(User user);

    boolean checkUsername(String username);

    boolean checkEmail(String email);

    LoginInfo login(String account,String password);

    Result getUserInfoById(Integer userId);

    Result updataUsername(Integer userId, String username);
}

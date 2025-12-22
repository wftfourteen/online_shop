package com.fourteen.service;

import com.fourteen.pojo.LoginInfo;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    Result addUser(User user);

    boolean checkUsername(String username);

    boolean checkEmail(String email);

    LoginInfo login(String account,String password);

    Result getUserInfoById(Integer userId);

    Result updataUsername(Integer userId, String username);
    
    Result uploadAvatar(Integer userId, MultipartFile file);
}

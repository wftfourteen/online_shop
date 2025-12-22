package com.fourteen.controller;

import com.fourteen.pojo.LoginInfo;
import com.fourteen.pojo.Result;
import com.fourteen.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping
    public Result login(@RequestBody Map<String, String> loginRequest){
        String account = loginRequest.get("account");
        String password = loginRequest.get("password");
        log.info("用户登录：{},{}",account,password);
        LoginInfo loginInfo = userService.login(account,password);
        if(loginInfo!=null){
            // 获取用户完整信息以获取头像
            com.fourteen.pojo.Result userResult = userService.getUserInfoById(loginInfo.getUserId());
            String avatar = "/default-avatar.png";
            if (userResult.getCode() == 1 && userResult.getData() != null) {
                com.fourteen.pojo.User user = (com.fourteen.pojo.User) userResult.getData();
                if (user.getAvatar() != null) {
                    avatar = user.getAvatar();
                }
            }
            
            // 转换返回格式，符合接口文档要求
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("userId", loginInfo.getUserId());
            data.put("username", loginInfo.getUserName());
            data.put("role", loginInfo.getRole());
            data.put("avatar", avatar);
            data.put("token", loginInfo.getToken());
            return Result.success(data);
        }
        return Result.error("账号或密码不正确");
    }
}

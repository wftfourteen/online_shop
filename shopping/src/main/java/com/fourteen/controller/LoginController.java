package com.fourteen.controller;

import com.fourteen.pojo.LoginInfo;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;
import com.fourteen.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping
    public Result login(@RequestParam  String account,@RequestParam String password ){
        log.info("用户登录：{},{}",account,password);
        LoginInfo loginInfo = userService.login(account,password);
        if(loginInfo!=null){
            return Result.success(loginInfo);
        }
        return Result.error("账号或密码错误");
    }
}

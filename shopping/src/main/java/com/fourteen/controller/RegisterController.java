package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;
import com.fourteen.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/check-username")
    public Result checkUsername(@RequestParam String username){
        log.info("check username:"+username);
        if(userService.checkUsername(username)){
            return Result.success();
        }
        else
            return Result.error("用户名已存在");
    }

    @GetMapping("/check-email")
    public Result checkEmail(@RequestParam String email){
        log.info("check email:"+email);
        if(userService.checkEmail(email)){
            return Result.success();
        }
        else
            return Result.error("邮箱已注册");
    }

    /**
     * 用户注册
     */
    @PostMapping
    public Result Register(@RequestBody User user) {
        log.info("用户注册:{}",user);
        return userService.addUser(user);
    }

}

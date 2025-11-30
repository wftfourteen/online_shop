package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public Result getUserInfo(HttpServletRequest request){
        Integer userId = (Integer)request.getAttribute("userId");
        log.info("请求用户个人信息：{}",userId);
        return userService.getUserInfoById(userId);
    }

    @PutMapping
    public  Result updateUsername(HttpServletRequest request, @RequestParam String username){
        Integer userId = (Integer)request.getAttribute("userId");
        log.info("更改个人昵称{}",userId);
        return userService.updataUsername(userId,username);
    }

    /**
     * 退出登录接口
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("用户退出登录: userId={}", userId);
        // 退出登录主要是前端清除Token，后端无需特殊处理
        // 如果后续需要实现Token黑名单等功能，可以在这里添加
        return Result.success();
    }
}

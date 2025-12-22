package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Result getUserInfo(HttpServletRequest request){
        Integer userId = (Integer)request.getAttribute("userId");
        log.info("请求用户个人信息：{}",userId);
        return userService.getUserInfoById(userId);
    }

    @PutMapping
    public Result updateUserInfo(HttpServletRequest request, @RequestBody Map<String, String> updateRequest){
        Integer userId = (Integer)request.getAttribute("userId");
        String username = updateRequest.get("username");
        log.info("更改个人信息 userId={}, username={}", userId, username);
        if (username != null) {
            return userService.updataUsername(userId, username);
        }
        return Result.success();
    }

    /**
     * 头像上传接口
     */
    @PostMapping("/avatar")
    public Result uploadAvatar(HttpServletRequest request, @RequestParam("avatar") MultipartFile file) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("用户上传头像: userId={}", userId);
        return userService.uploadAvatar(userId, file);
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

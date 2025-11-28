package com.fourteen.controller;

import com.fourteen.pojo.Result;
import com.fourteen.service.UserService;
import io.jsonwebtoken.ClaimJwtException;
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
    ClaimJwtException request;

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
}

package com.fourteen.service.impl;

import com.fourteen.mapper.UserMapper;
import com.fourteen.pojo.LoginInfo;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;
import com.fourteen.service.UserService;
import com.fourteen.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    // 邮箱正则（简化版）
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    //判断邮箱的简单方法
    private boolean isEmail(String account) {
        // 简单的邮箱格式验证
        return account != null &&
                account.contains("@") &&
                account.contains(".") &&
                EMAIL_PATTERN.matcher(account).matches();
    }

    @Override
    public boolean checkUsername(String username) {
        if(userMapper.findByUsername(username)==null)
            return true;
        return false;
    }

    @Override
    public boolean checkEmail(String email) {
        if(userMapper.findByEmail(email)==null)
            return true;
        return false;
    }

    @Override
    public LoginInfo login(String account,String password) {
        User u= null;
        if(isEmail(account)) u= userMapper.findByEmailAndPassword(account,password);
        else u=userMapper.findByUsernameAndPassword(account,password);
        if(u!=null){
            log.info("登录成功，用户信息：{}",u);
            //生成JWT令牌
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",u.getUserId());
            claims.put("username",u.getUsername());
            claims.put("role",u.getRole());
            String jwt= JwtUtils.GenerateToken(claims);
            return new LoginInfo(u.getUserId(),u.getUsername(),u.getEmail(),u.getRole(),jwt);
        }
        return null;
    }


    @Override
    public Result addUser(User user) {
        //检查用户名和邮箱
        if(userMapper.findByUsername(user.getUsername())!=null || userMapper.findByEmail(user.getEmail())!=null)
            return Result.error("用户名或邮箱已注册");
        //添加信息
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        if(user.getAvatar()==null) user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=default&size=150");
        //调用mapper
        userMapper.addUser(user);
        return Result.success();
    }


    @Override
    public Result getUserInfoById(Integer userId) {
        User user =userMapper.findByUserId(userId);
        if(user==null) {
            return Result.error("用户不存在");
        }
        if(user.getStatus()==0) {
            return Result.error("用户被禁用，请重新登录");
        }
        //密码不能传
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result updataUsername(Integer userId, String username) {
        if(userMapper.findByUsername(username)!=null){
            return Result.error("用户名已存在");
        }
        userMapper.updateUsername(userId, username);
        return Result.success();
    }

}

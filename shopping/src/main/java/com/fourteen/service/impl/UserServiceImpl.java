package com.fourteen.service.impl;

import com.fourteen.mapper.UserMapper;
import com.fourteen.pojo.LoginInfo;
import com.fourteen.pojo.Result;
import com.fourteen.pojo.User;
import com.fourteen.service.UserService;
import com.fourteen.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Value("${file.upload.path:./uploads/avatars}")
    private String uploadPath;

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
            LoginInfo loginInfo = new LoginInfo(u.getUserId(),u.getUsername(),u.getEmail(),u.getRole(),jwt);
            // 设置头像（通过反射或修改LoginInfo类）
            return loginInfo;
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

    @Override
    public Result uploadAvatar(Integer userId, MultipartFile file) {
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }
            
            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.error("文件名无效");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
                return Result.error("图片格式不支持，请上传 JPG/PNG 格式");
            }
            
            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error("图片大小超过限制，请上传≤5MB的图片");
            }
            
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(fileName);
            
            // 保存文件
            Files.copy(file.getInputStream(), filePath);
            
            // 更新用户头像URL
            String avatarUrl = "/avatars/" + fileName;
            userMapper.updateAvatar(userId, avatarUrl);
            
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", avatarUrl);
            Result result = Result.success(data);
            result.setMsg("头像上传成功");
            return result;
            
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }

}

package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;
    private String username;
    private String email;
    private String password;
    private Integer role; // 1:顾客 2:商家
    private String avatar;
    private Integer status; // 1:正常 0:禁用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
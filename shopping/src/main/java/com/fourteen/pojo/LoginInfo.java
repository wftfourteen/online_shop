package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.annotations.Insert;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    private Integer userId;
    private String userName;
    private String email;
    private Integer role;
    private String token;
}

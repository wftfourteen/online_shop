package com.fourteen.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET_KEY = "ThisIsAVeryLongSecretKeyThatIsAtLeast32Characters!!";
    private static final long EXPIRATION_TIME = 12*60*60*1000;

    public static String GenerateToken(Map<String,Object> claims) {
        return Jwts.builder().signWith(SignatureAlgorithm.HS256,SECRET_KEY)//设置密钥
                .addClaims(claims)//自定义信息
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME) )//设置过期使劲按
                .compact();
    }
    public static Claims ParseToken(String token) {
         return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}

package com.fourteen;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class JwtTest {

    @Test
    public void testGenerateJwt() {
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("id",1);
        dataMap.put("username","admin");
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS256,"ThisIsAVeryLongSecretKeyThatIsAtLeast32Characters!!")//设置密钥
                .addClaims(dataMap)//自定义信息
                .setExpiration(new Date(System.currentTimeMillis()+3600*1000))//设置过期使劲按
                .compact();
        System.out.println(jwt);
    }
    @Test
    public void testParseJwt() {
        String token="eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJhZG1pbiIsImV4cCI6MTc2NDMwMTczN30.l5tswmdRCX5QKC2TnvVS7dCpr1bGWDg7pGM9PRkQaWo";
        Claims claims= Jwts.parser().setSigningKey("ThisIsAVeryLongSecretKeyThatIsAtLeast32Characters!!")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
    }
}

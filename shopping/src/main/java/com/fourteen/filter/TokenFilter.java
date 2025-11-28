package com.fourteen.filter;

import com.fourteen.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
@Slf4j
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();//资源访问路径 e.g. /login/emp
        String method = request.getMethod();
        
        log.info("TokenFilter拦截请求: {} {}", method, requestURI);

        // 公开接口，不需要Token验证
        if (isPublicEndpoint(requestURI, method)) {
            log.info("公开接口，放行: {} {}", method, requestURI);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            log.info("令牌为空，响应401: {}", requestURI);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"未登录或Token无效\",\"data\":null}");
            return;
        }
        try{
            // 解析token并提取用户信息
            io.jsonwebtoken.Claims claims = JwtUtils.ParseToken(token);
            
            // 将用户信息存储到request的attribute中，方便后续使用
            Object userIdObj = claims.get("id");
            if (userIdObj != null) {
                Integer userId = userIdObj instanceof Integer ? 
                    (Integer) userIdObj : ((Number) userIdObj).intValue();
                request.setAttribute("userId", userId);
            }
            
            // 存储用户名
            String username = claims.get("username", String.class);
            if (username != null) {
                request.setAttribute("username", username);
            }
            
            // 存储用户角色
            Object roleObj = claims.get("role");
            if (roleObj != null) {
                Integer role = roleObj instanceof Integer ? 
                    (Integer) roleObj : ((Number) roleObj).intValue();
                request.setAttribute("role", role);
            }
            
            log.info("令牌验证成功，用户ID: {}, 放行: {}", request.getAttribute("userId"), requestURI);
        }catch (Exception e){
            log.info("令牌非法，响应401: {}, 错误: {}", requestURI, e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":0,\"msg\":\"Token无效或已过期\",\"data\":null}");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 判断是否为公开接口（不需要Token验证）
     */
    private boolean isPublicEndpoint(String requestURI, String method) {
        // POST /login - 登录接口
        if (requestURI.equals("/login") && "POST".equals(method)) {
            return true;
        }
        // POST /register - 注册接口
        if (requestURI.equals("/register") && "POST".equals(method)) {
            return true;
        }
        // GET /register/check-username - 用户名验证接口
        if (requestURI.equals("/register/check-username") && "GET".equals(method)) {
            return true;
        }
        // GET /register/check-email - 邮箱验证接口
        if (requestURI.equals("/register/check-email") && "GET".equals(method)) {
            return true;
        }
        return false;
    }
}

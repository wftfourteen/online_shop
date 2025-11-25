package com.fourteen.config;

/**
 * 系统常量配置
 */
public class Constants {

    /**
     * 用户角色常量
     */
    public static class UserRoles {
        public static final int CUSTOMER = 1;    // 顾客
        public static final int BUSINESS = 2;    // 商家
        public static final int ADMIN = 3;       // 管理员

        private UserRoles() {} // 防止实例化
    }

    /**
     * 用户状态常量
     */
    public static class UserStatus {
        public static final int ACTIVE = 1;      // 正常
        public static final int DISABLED = 0;    // 禁用

        private UserStatus() {}
    }

    /**
     * 商品状态常量
     */
    public static class ProductStatus {
        public static final int LISTED = 1;      // 上架
        public static final int UNLISTED = 0;    // 下架

        private ProductStatus() {}
    }

    /**
     * 订单状态常量
     */
    public static class OrderStatus {
        public static final int PENDING = 1;     // 待支付
        public static final int PAID = 2;        // 已支付
        public static final int SHIPPED = 3;     // 已发货
        public static final int COMPLETED = 4;   // 已完成
        public static final int CANCELLED = 5;   // 已取消

        private OrderStatus() {}
    }

    /**
     * HTTP响应状态码
     */
    public static class ResponseCodes {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int SERVER_ERROR = 500;

        private ResponseCodes() {}
    }

    /**
     * Token相关常量
     */
    public static class Token {
        public static final String HEADER_NAME = "token";
        public static final int EXPIRATION_HOURS = 24; // token过期时间24小时

        private Token() {}
    }

    // 防止实例化
    private Constants() {}
}

# 电商购物平台系统

一个基于Spring Boot + MyBatis + Vue的前后端分离电商购物平台，支持顾客和商家双角色，提供完整的商品管理、购物车、订单管理等功能。

## 项目简介

本项目是一个功能完整的电商购物平台系统，采用前后端分离架构，支持以下核心功能：

- **用户管理**：用户注册、登录、个人信息管理（支持顾客/商家双角色）
- **商品管理**：商品展示、搜索、分类筛选、商品详情、商家商品管理
- **购物车**：购物车增删改查
- **订单管理**：订单创建、支付、订单查询、订单状态管理
- **文件上传**：基于阿里云OSS的头像和商品图片上传
- **邮件通知**：订单支付成功后自动发送邮件确认

## 技术栈

### 后端技术
- **框架**：Spring Boot 3.5.8
- **ORM**：MyBatis 3.0.5
- **数据库**：MySQL 8.0
- **认证**：JWT (JSON Web Token)
- **文件存储**：阿里云OSS
- **邮件服务**：Spring Mail
- **分页插件**：PageHelper 1.4.7
- **Java版本**：Java 17

### 前端技术
- **框架**：Vue 3
- **UI组件**：Element Plus
- **HTTP客户端**：Axios
- **路由**：Vue Router

## 项目结构

```
shopping/
├── src/main/java/com/fourteen/
│   ├── config/              # 配置类
│   │   ├── Constants.java   # 系统常量
│   │   └── CorsConfig.java  # 跨域配置
│   ├── controller/          # 控制器层
│   │   ├── LoginController.java
│   │   ├── RegisterController.java
│   │   ├── UserController.java
│   │   ├── ProductsController.java
│   │   ├── ShoppingCartController.java
│   │   ├── OrderController.java
│   │   ├── PaymentController.java
│   │   ├── AdminProductController.java
│   │   ├── AdminOrderController.java
│   │   └── UserAddressController.java
│   ├── service/             # 服务层接口
│   ├── service/impl/        # 服务层实现
│   ├── mapper/              # MyBatis Mapper接口
│   ├── pojo/                # 实体类
│   ├── utils/               # 工具类
│   │   └── JwtUtils.java    # JWT工具类
│   ├── filter/              # 过滤器
│   │   └── TokenFilter.java # Token验证过滤器
│   └── ShoppingApplication.java  # 启动类
├── src/main/resources/
│   ├── application.yml       # 应用配置
│   └── com.fourteen.mapper/  # MyBatis XML映射文件
├── frontend/                 # 前端代码（已删除，需要单独部署）
└── pom.xml                   # Maven依赖配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- 阿里云OSS账号（用于文件上传）

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE online_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行建表SQL脚本（参考数据库表设计文档）

3. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_shop
    username: root
    password: your_password
```

### 阿里云OSS配置

在 `application.yml` 中配置OSS信息：

```yaml
aliyun:
  oss:
    endpoint: https://oss-cn-beijing.aliyuncs.com
    access-key-id: your_access_key_id
    access-key-secret: your_access_key_secret
    bucket-name: your_bucket_name
    url-prefix: https://your_bucket_name.oss-cn-beijing.aliyuncs.com
```

### 邮件配置（可选）

如需启用邮件功能，在 `application.yml` 中配置：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your_email@qq.com
    password: your_email_auth_code
    from: your_email@qq.com
```

### 运行项目

1. 克隆项目：
```bash
git clone <repository-url>
cd shopping
```

2. 编译项目：
```bash
mvn clean compile
```

3. 运行项目：
```bash
mvn spring-boot:run
```

4. 访问API：
- 后端API地址：http://localhost:1414
- API文档：参考项目中的API接口文档

## 主要功能模块

### 1. 用户模块
- 用户注册（支持用户名/邮箱注册）
- 用户登录（JWT认证）
- 个人信息管理
- 头像上传
- 角色管理（顾客/商家）

### 2. 商品模块
- 商品列表展示（分页、排序、筛选）
- 商品搜索
- 商品详情查看
- 商家商品管理（增删改查、上下架、图片上传）

### 3. 购物车模块
- 添加商品到购物车
- 查看购物车列表
- 修改商品数量
- 删除购物车商品

### 4. 订单模块
- 创建订单
- 订单支付（模拟支付）
- 订单查询（列表、详情）
- 订单状态管理（取消、确认收货）
- 订单邮件通知

### 5. 收货地址模块
- 添加收货地址
- 修改收货地址
- 删除收货地址
- 查询收货地址列表

## API接口说明

### 公开接口（无需Token）
- `POST /login` - 用户登录
- `POST /register` - 用户注册
- `GET /register/check-username` - 检查用户名
- `GET /register/check-email` - 检查邮箱
- `GET /products` - 商品列表
- `GET /products/{id}` - 商品详情

### 需要Token的接口
- 用户相关：`/user/*`
- 购物车相关：`/cart/*`
- 订单相关：`/orders/*`
- 支付相关：`/payment/*`
- 地址相关：`/addresses/*`
- 商家管理：`/admin/*`

详细API文档请参考项目中的API接口文档。

## 系统特点

1. **前后端分离**：后端提供RESTful API，前端独立部署
2. **JWT认证**：基于Token的无状态认证机制
3. **角色权限**：支持顾客和商家双角色，不同角色访问不同功能
4. **文件存储**：使用阿里云OSS存储用户头像和商品图片
5. **分页查询**：使用PageHelper实现高效的分页查询
6. **事务管理**：订单创建等关键操作使用事务保证数据一致性
7. **邮件通知**：订单支付成功后自动发送邮件

## 开发规范

- 遵循RESTful API设计规范
- 统一使用Result类封装返回结果
- 使用Lombok简化代码
- 使用MyBatis进行数据库操作
- 关键业务逻辑使用事务注解

## 注意事项

1. 生产环境请修改JWT密钥
2. 生产环境请使用HTTPS
3. OSS配置信息请妥善保管
4. 数据库密码请使用强密码
5. 建议配置Nginx反向代理

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题，请提交Issue。


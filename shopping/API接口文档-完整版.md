# 在线购物平台 API 接口文档（完整版）

**基础URL**: `http://localhost:1414`

**认证方式**: JWT Token（Bearer Token）

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 目录

1. [用户认证模块](#1-用户认证模块)
2. [商品模块](#2-商品模块)
3. [购物车模块](#3-购物车模块)
4. [地址管理模块](#4-地址管理模块)
5. [订单模块](#5-订单模块)
6. [支付模块](#6-支付模块)
7. [商家管理模块](#7-商家管理模块)

---

## 1. 用户认证模块

### 1.1 用户注册

**接口地址**: `POST /register`

**请求参数**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "role": 1
}
```

| 参数名   | 类型   | 必填 | 说明           |
| -------- | ------ | ---- | -------------- |
| username | string | 是   | 用户名，3-20位 |
| email    | string | 是   | 邮箱地址       |
| password | string | 是   | 密码，至少6位  |
| role     | number | 是   | 角色：1-顾客，2-商家 |

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 1.2 用户名验证

**接口地址**: `GET /register/check-username?username={username}`

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

### 1.3 邮箱验证

**接口地址**: `GET /register/check-email?email={email}`

---

### 1.4 用户登录

**接口地址**: `POST /login`

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "role": 1,
    "avatar": "/avatars/default.png",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

### 1.5 获取用户信息

**接口地址**: `GET /user`

**认证**: 需要Token

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatar": "/avatars/user.jpg",
    "role": 1,
    "status": 1
  }
}
```

---

### 1.6 更新用户信息

**接口地址**: `PUT /user`

**认证**: 需要Token

**请求参数**:
```json
{
  "username": "newname"
}
```

---

### 1.7 上传头像

**接口地址**: `POST /user/avatar`

**认证**: 需要Token

**请求格式**: `multipart/form-data`

| 参数名 | 类型 | 必填 | 说明            |
| ------ | ---- | ---- | --------------- |
| avatar | file | 是   | 头像文件，≤5MB |

---

### 1.8 退出登录

**接口地址**: `POST /user/logout`

**认证**: 需要Token

---

## 2. 商品模块

### 2.1 获取商品列表

**接口地址**: `GET /products`

**请求参数**:
| 参数名   | 类型   | 必填 | 说明                             |
| -------- | ------ | ---- | -------------------------------- |
| page     | number | 是   | 页码，从1开始                    |
| pageSize | number | 是   | 每页数量                         |
| minPrice | number | 否   | 最低价格                         |
| maxPrice | number | 否   | 最高价格                         |
| sort     | string | 否   | 排序：default/price_asc/price_desc |

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "list": [
      {
        "productId": 1,
        "mainImage": "/images/product1.jpg",
        "name": "商品名称",
        "price": 299.99,
        "stockStatus": 100
      }
    ],
    "total": 45,
    "totalPage": 4,
    "currentPage": 1
  }
}
```

---

### 2.2 搜索商品

**接口地址**: `GET /products/search?keyword={keyword}&page={page}&pageSize={pageSize}`

---

### 2.3 获取商品详情

**接口地址**: `GET /products/{productId}`

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "productId": 1,
    "name": "商品名称",
    "price": 299.99,
    "stock": 100,
    "images": ["/images/main.jpg", "/images/detail1.jpg"],
    "description": "商品描述"
  }
}
```

---

## 3. 购物车模块

### 3.1 添加商品到购物车

**接口地址**: `POST /cart`

**认证**: 需要Token

**请求参数**:
```json
{
  "productId": 1,
  "quantity": 2
}
```

---

### 3.2 获取购物车列表

**接口地址**: `GET /cart`

**认证**: 需要Token

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "cartId": 1,
      "productId": 1,
      "name": "商品名称",
      "mainImage": "/images/product.jpg",
      "price": 299.99,
      "quantity": 2,
      "stock": 100
    }
  ]
}
```

---

### 3.3 更新购物车商品数量

**接口地址**: `PUT /cart/{cartId}`

**认证**: 需要Token

**请求参数**:
```json
{
  "quantity": 3
}
```

---

### 3.4 删除购物车商品

**接口地址**: `DELETE /cart/{cartId}`

**认证**: 需要Token

---

## 4. 地址管理模块

### 4.1 获取地址列表

**接口地址**: `GET /addresses`

**认证**: 需要Token

---

### 4.2 添加地址

**接口地址**: `POST /addresses`

**认证**: 需要Token

**请求参数**:
```json
{
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detail": "科技园路123号"
}
```

---

### 4.3 更新地址

**接口地址**: `PUT /addresses/{addressId}`

**认证**: 需要Token

---

### 4.4 删除地址

**接口地址**: `DELETE /addresses/{addressId}`

**认证**: 需要Token

---

## 5. 订单模块

### 5.1 创建订单

**接口地址**: `POST /orders`

**认证**: 需要Token

**说明**: 创建订单（状态为待支付）

**请求参数**:
```json
{
  "addressId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "cartId": 1
    }
  ],
  "remark": "订单备注"
}
```

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderId": 123,
    "totalAmount": 599.98
  }
}
```

---

### 5.2 创建订单并支付（合并接口）

**接口地址**: `POST /orders/create-and-pay`

**认证**: 需要Token

**说明**: 创建订单并立即支付（模拟支付，直接返回成功），用于快速购买流程。支付成功后会自动：
- 更新订单状态为已支付
- 发送支付成功确认邮件到用户邮箱
- 删除已购买商品的购物车项（如果提供了cartId）

**请求参数**:
```json
{
  "addressId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "cartId": 1
    }
  ],
  "remark": "订单备注",
  "paymentMethod": "alipay"
}
```

| 参数名        | 类型   | 必填 | 说明                    |
| ------------- | ------ | ---- | ----------------------- |
| addressId     | number | 是   | 收货地址ID              |
| items         | array  | 是   | 订单商品列表            |
| remark        | string | 否   | 订单备注                |
| paymentMethod | string | 否   | 支付方式，默认alipay    |

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderId": 123,
    "status": 2,
    "paymentMethod": "alipay",
    "amount": 599.98,
    "message": "支付成功！"
  }
}
```

---

### 5.3 获取订单列表

**接口地址**: `GET /orders?status={status}`

**认证**: 需要Token

| 参数名 | 类型   | 必填 | 说明                    |
| ------ | ------ | ---- | ----------------------- |
| status | number | 否   | 订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消 |

---

### 5.4 获取订单详情

**接口地址**: `GET /orders/{orderId}`

**认证**: 需要Token

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderId": 123,
    "totalAmount": 599.98,
    "status": 2,
    "address": {
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "detailAddress": "科技园路123号"
    },
    "items": [
      {
        "productId": 1,
        "productName": "商品名称",
        "productImage": "/images/product.jpg",
        "price": 299.99,
        "quantity": 2,
        "subtotal": 599.98
      }
    ],
    "createdAt": "2025-01-01T10:00:00",
    "updatedAt": "2025-01-01T10:00:00"
  }
}
```

---

### 5.5 取消订单

**接口地址**: `PUT /orders/{orderId}/cancel`

**认证**: 需要Token

---

### 5.6 确认收货

**接口地址**: `PUT /orders/{orderId}/confirm`

**认证**: 需要Token

**说明**: 只有已发货（status=3）的订单可以确认收货，确认后订单状态变为已完成（status=4）

---

## 6. 支付模块

### 6.1 支付订单

**接口地址**: `POST /payment/pay`

**认证**: 需要Token

**说明**: 支付订单（模拟支付，直接返回成功），支付成功后会自动发送确认邮件

**请求参数**:
```json
{
  "orderId": 1,
  "paymentMethod": "alipay"
}
```

| 参数名        | 类型   | 必填 | 说明                    |
| ------------- | ------ | ---- | ----------------------- |
| orderId       | number | 是   | 订单ID                  |
| paymentMethod | string | 是   | 支付方式：alipay/wechat |

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderId": 1,
    "status": 2,
    "paymentMethod": "alipay",
    "amount": 599.98,
    "message": "支付成功！"
  }
}
```

---

### 6.2 查询支付状态

**接口地址**: `GET /payment/status/{orderId}`

**认证**: 需要Token

**响应示例**:
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderId": 1,
    "status": 2,
    "totalAmount": 599.98
  }
}
```

---

**接口地址**: `GET /payment/status/{orderId}`

**认证**: 需要Token

---

## 7. 商家管理模块

### 7.1 获取商品列表（商家）

**接口地址**: `GET /admin/products`

**认证**: 需要Token（商家角色）

**请求参数**: 同商品列表接口

---

### 7.2 添加商品

**接口地址**: `POST /admin/products`

**认证**: 需要Token（商家角色）

**请求参数**:
```json
{
  "name": "商品名称",
  "price": 299.99,
  "stock": 100,
  "mainImage": "/images/product.jpg",
  "description": "商品描述",
  "categoryId": 1
}
```

---

### 7.3 更新商品

**接口地址**: `PUT /admin/products/{productId}`

**认证**: 需要Token（商家角色）

---

### 7.4 删除商品

**接口地址**: `DELETE /admin/products/{productId}`

**认证**: 需要Token（商家角色）

---

### 7.5 更新商品状态（上架/下架）

**接口地址**: `PUT /admin/products/{productId}/status`

**认证**: 需要Token（商家角色）

**请求参数**:
```json
{
  "status": 1
}
```

| status | 说明 |
| ------ | ---- |
| 0      | 下架 |
| 1      | 上架 |

---

### 7.6 更新商品库存

**接口地址**: `PUT /admin/products/{productId}/stock`

**认证**: 需要Token（商家角色）

**请求参数**:
```json
{
  "stock": 150
}
```

---

### 7.7 获取订单列表（商家）

**接口地址**: `GET /admin/orders?page={page}&pageSize={pageSize}&status={status}`

**认证**: 需要Token（商家角色）

---

### 7.8 获取订单详情（商家）

**接口地址**: `GET /admin/orders/{orderId}`

**认证**: 需要Token（商家角色）

---

### 7.9 订单发货

**接口地址**: `PUT /admin/orders/{orderId}/ship`

**认证**: 需要Token（商家角色）

**请求参数**:
```json
{
  "trackingNumber": "SF1234567890",
  "shippingCompany": "顺丰快递"
}
```

---

## 响应码说明

| code | 说明     |
| ---- | -------- |
| 1    | 成功     |
| 0    | 失败     |

## 错误处理

所有接口错误响应格式：
```json
{
  "code": 0,
  "msg": "错误信息",
  "data": null
}
```

常见错误码：
- 401: 未授权，Token无效或过期
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

---

**文档版本**: v1.0  
**最后更新**: 2025年

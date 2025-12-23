# 前端使用说明

## 项目结构

```
frontend/
├── index.html          # 欢迎页面
├── login.html          # 登录页面
├── register.html       # 注册页面
├── customer.html       # 顾客端-商品列表页
├── product-detail.html # 商品详情页
├── cart.html           # 购物车页面
├── profile.html        # 个人中心页面
├── merchant.html       # 商家管理页面
├── css/
│   └── style.css       # 样式文件
├── js/
│   ├── api.js          # API接口封装
│   ├── welcome.js      # 欢迎页脚本
│   ├── login.js        # 登录页脚本
│   ├── register.js     # 注册页脚本
│   ├── customer.js     # 商品列表页脚本
│   ├── product-detail.js # 商品详情页脚本
│   ├── cart.js         # 购物车脚本
│   └── merchant.js     # 商家管理脚本
└── README.md           # 本文件
```

## 功能说明

### 1. 欢迎页面 (index.html)
- 展示平台特色
- 提供登录/注册入口

### 2. 登录页面 (login.html)
- 支持用户名/邮箱登录
- 身份选择（顾客/商家）
- 根据身份跳转到不同页面

### 3. 注册页面 (register.html)
- 用户注册
- 实时验证用户名和邮箱
- 身份选择（顾客/商家）

### 4. 顾客端

#### 商品列表页 (customer.html)
- 商品展示（网格布局）
- 价格筛选
- 排序功能
- 搜索功能
- 分页显示

#### 商品详情页 (product-detail.html)
- 商品详细信息
- 图片轮播
- 购买数量选择
- 加入购物车
- 立即购买

#### 购物车页面 (cart.html)
- 查看购物车商品
- 修改数量
- 删除商品
- 结算功能

#### 个人中心 (profile.html)
- 查看个人信息
- 修改用户名
- 上传头像

### 5. 商家端

#### 商家管理页面 (merchant.html)
- 商品管理
  - 商品列表
  - 添加商品
  - 编辑商品
  - 上架/下架
  - 删除商品
- 订单管理
  - 订单列表
  - 订单详情
  - 发货功能

## 技术栈

- **Vue 3** (CDN方式)
- **Element Plus** (UI组件库)
- **Axios** (HTTP请求)
- **原生JavaScript**

## 使用说明

1. 确保后端服务运行在 `http://localhost:1414`

2. 配置API地址（如需修改）：
   - 编辑 `js/api.js` 中的 `API_CONFIG.BASE_URL`

3. 直接打开HTML文件或使用本地服务器：
   ```bash
   # 使用VS Code Live Server插件
   # 或使用Python简单服务器
   python -m http.server 8080
   ```

4. 访问：
   - 欢迎页：`http://localhost:8080/index.html`
   - 登录页：`http://localhost:8080/login.html`

## 注意事项

1. **跨域问题**：如果遇到跨域问题，需要后端配置CORS
2. **Token存储**：Token存储在localStorage中
3. **身份验证**：所有需要认证的接口会自动添加Token到请求头
4. **路由跳转**：使用简单的页面跳转，未使用Vue Router

## 开发建议

1. 建议使用本地服务器运行（避免file://协议导致的CORS问题）
2. 生产环境建议使用构建工具打包
3. 可以将多个页面整合为SPA（单页应用）

## 浏览器兼容性

- Chrome ≥90
- Firefox ≥88
- Safari ≥14
- Edge ≥90

# 阿里云OSS配置说明

## 1. 配置步骤

### 1.1 在 application.yml 中配置OSS信息

编辑 `src/main/resources/application.yml`，修改以下配置：

```yaml
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com  # 根据你的OSS区域修改，如：oss-cn-beijing.aliyuncs.com
    access-key-id: your-access-key-id  # 替换为你的AccessKey ID
    access-key-secret: your-access-key-secret  # 替换为你的AccessKey Secret
    bucket-name: your-bucket-name  # 替换为你的Bucket名称
    url-prefix: https://your-bucket-name.oss-cn-hangzhou.aliyuncs.com  # OSS访问URL前缀，替换为你的Bucket域名
```

### 1.2 获取AccessKey

1. 登录阿里云控制台
2. 进入"访问控制" -> "用户"
3. 创建用户或使用现有用户
4. 为用户添加"AliyunOSSFullAccess"权限
5. 创建AccessKey，获取AccessKey ID和AccessKey Secret

### 1.3 创建OSS Bucket

1. 登录阿里云OSS控制台
2. 创建Bucket（如果还没有）
3. 设置Bucket为"公共读"权限（如果需要公网访问）
4. 记录Bucket名称和Endpoint

### 1.4 配置Bucket域名（可选）

如果需要自定义域名：
1. 在OSS控制台的Bucket设置中配置自定义域名
2. 将 `url-prefix` 配置为自定义域名

## 2. 文件夹结构

OSS中的文件存储结构：
- `avatars/` - 用户头像
- `products/` - 商品图片（主图和详情图）

## 3. 注意事项

1. **安全性**：生产环境中，建议将AccessKey配置在环境变量中，不要直接写在配置文件中
2. **权限**：建议使用最小权限原则，只授予必要的OSS权限
3. **跨域**：如果前端和OSS不在同一域名，需要在OSS控制台配置CORS规则
4. **费用**：注意OSS的存储和流量费用

## 4. 测试

配置完成后，重启应用，上传图片测试是否能正常上传和访问。


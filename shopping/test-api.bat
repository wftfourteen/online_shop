@echo off
chcp 65001 >nul
echo ========================================
echo     电商系统 API 测试脚本
echo ========================================
echo.

set BASE_URL=http://localhost:1414

echo [1] 测试用户注册接口...
curl -X POST %BASE_URL%/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"role\":1}"
echo.
echo.

echo [2] 测试检查用户名接口...
curl "%BASE_URL%/register/check-username?username=testuser"
echo.
echo.

echo [3] 测试检查邮箱接口...
curl "%BASE_URL%/register/check-email?email=test@example.com"
echo.
echo.

echo [4] 测试用户登录接口...
curl -X POST %BASE_URL%/login ^
  -H "Content-Type: application/json" ^
  -d "{\"account\":\"testuser\",\"password\":\"password123\"}"
echo.
echo.

echo [5] 测试获取商品列表接口...
curl "%BASE_URL%/products?page=1&pageSize=10"
echo.
echo.

echo ========================================
echo     测试完成
echo ========================================
echo.
echo 注意：如果看到 curl 命令不存在，请：
echo 1. 安装 curl 工具
echo 2. 或者使用 Postman 等工具进行测试
echo 3. 或者直接在浏览器访问前端页面进行测试
echo.
pause


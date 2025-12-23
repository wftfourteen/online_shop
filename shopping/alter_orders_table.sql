-- 修改订单表，使 address_id 字段允许为 NULL
-- 执行此脚本以使订单可以在没有收货地址的情况下创建

USE online_shop;

ALTER TABLE orders MODIFY COLUMN address_id INT NULL;

-- 验证修改
-- 执行以下查询应该看到 address_id 的 NULL 列显示为 YES
-- DESCRIBE orders;

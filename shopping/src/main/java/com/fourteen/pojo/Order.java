package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Integer orderId;
    private Integer userId;
    private Double totalAmount;
    private Integer status; // 1:待支付 2:已支付 3:已发货 4:已完成 5:已取消
    private Integer addressId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

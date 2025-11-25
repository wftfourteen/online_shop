package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    private Integer cartId;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private LocalDateTime createdAt;
}
package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer productId;
    private String name;
    private Integer categoryId;
    private Double price;
    private Integer stock;
    private String mainImage;
    private String detailImages; // JSON字符串或逗号分隔的URL
    private String description;
    private Integer status; // 1:上架 0:下架
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
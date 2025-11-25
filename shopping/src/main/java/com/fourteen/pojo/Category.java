package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Integer categoryId;
    private String name;
    private Integer parentId;
    private Integer sortOrder;
    private Integer status; // 1:启用 0:禁用
    private LocalDateTime createdAt;
}
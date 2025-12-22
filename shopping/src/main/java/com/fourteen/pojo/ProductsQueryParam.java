package com.fourteen.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsQueryParam {
    private Integer page = 1;
    private Integer pageSize = 30;
    private Integer minPrice;
    private Integer maxPrice;
    private String name; // 商品名称关键词搜索
    private String sort;
    private Integer categoryId; // 分类ID筛选
}

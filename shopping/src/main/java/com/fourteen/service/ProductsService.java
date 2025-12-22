package com.fourteen.service;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;

public interface ProductsService {
    PageResult<Product> page(ProductsQueryParam productsQueryParam);
    
    Product getProductById(Integer productId);
}

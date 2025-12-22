package com.fourteen.service;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.pojo.Result;

public interface AdminProductService {
    Result addProduct(Product product);
    
    Result updateProduct(Product product);
    
    Result deleteProduct(Integer productId);
    
    PageResult<Product> getProductList(ProductsQueryParam param);
    
    Result getProductById(Integer productId);
    
    Result updateProductStatus(Integer productId, Integer status);
    
    Result updateProductStock(Integer productId, Integer stock);
}


package com.fourteen.service.impl;

import com.fourteen.mapper.ProductsMapper;
import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.service.ProductsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsServiceImpl implements ProductsService {
    @Autowired
    private ProductsMapper productsMapper;

    @Override
    public PageResult<Product> page(ProductsQueryParam productsQueryParam) {
        PageHelper.startPage(productsQueryParam.getPage(),productsQueryParam.getPageSize());
        List<Product> productList=productsMapper.list(productsQueryParam);
        Page<Product> p=(Page<Product>) productList;
        return new PageResult<>(p.getTotal(),productList);
    }

    @Override
    public Product getProductById(Integer productId) {
        return productsMapper.findById(productId);
    }
}

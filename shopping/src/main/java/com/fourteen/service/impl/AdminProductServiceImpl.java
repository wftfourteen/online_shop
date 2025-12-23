package com.fourteen.service.impl;

import com.fourteen.mapper.ProductsMapper;
import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.pojo.Result;
import com.fourteen.service.AdminProductService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AdminProductServiceImpl implements AdminProductService {
    
    @Autowired
    private ProductsMapper productsMapper;

    @Override
    public Result addProduct(Product product) {
        try {
            // 参数校验
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return Result.error("商品名称不能为空");
            }
            if (product.getPrice() == null || product.getPrice() <= 0) {
                return Result.error("商品价格必须大于0");
            }
            // categoryId 可以为null（如果数据库允许），但如果有约束则必须提供
            // 这里设置默认值1（假设1是默认分类）
            if (product.getCategoryId() == null) {
                product.setCategoryId(1); // 默认分类ID为1
            }
            
            // 设置默认值
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            if (product.getStatus() == null) {
                product.setStatus(1); // 默认上架
            }
            if (product.getStock() == null) {
                product.setStock(0);
            }
            if (product.getMainImage() == null || product.getMainImage().trim().isEmpty()) {
                product.setMainImage("https://via.placeholder.com/400x400?text=No+Image");
            }
            
            productsMapper.addProduct(product);
            log.info("添加商品成功：productId={}, name={}", product.getProductId(), product.getName());
            return Result.success(product);
        } catch (Exception e) {
            log.error("添加商品失败", e);
            return Result.error("添加商品失败：" + e.getMessage());
        }
    }

    @Override
    public Result updateProduct(Product product) {
        try {
            Product existingProduct = productsMapper.findById(product.getProductId());
            if (existingProduct == null) {
                return Result.error("商品不存在");
            }
            
            product.setUpdatedAt(LocalDateTime.now());
            productsMapper.updateProduct(product);
            log.info("更新商品成功：productId={}", product.getProductId());
            return Result.success();
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.error("更新商品失败：" + e.getMessage());
        }
    }

    @Override
    public Result deleteProduct(Integer productId) {
        try {
            Product product = productsMapper.findById(productId);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            productsMapper.deleteProduct(productId);
            log.info("删除商品成功：productId={}", productId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return Result.error("删除商品失败：" + e.getMessage());
        }
    }

    @Override
    public PageResult<Product> getProductList(ProductsQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<Product> productList = productsMapper.listAll(param); // 查询所有商品，包括下架的
        Page<Product> p = (Page<Product>) productList;
        return new PageResult<>(p.getTotal(), productList);
    }

    @Override
    public Result getProductById(Integer productId) {
        Product product = productsMapper.findById(productId);
        if (product == null) {
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }

    @Override
    public Result updateProductStatus(Integer productId, Integer status) {
        try {
            Product product = productsMapper.findById(productId);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            productsMapper.updateProductStatus(productId, status);
            log.info("更新商品状态成功：productId={}, status={}", productId, status);
            return Result.success();
        } catch (Exception e) {
            log.error("更新商品状态失败", e);
            return Result.error("更新商品状态失败：" + e.getMessage());
        }
    }

    @Override
    public Result updateProductStock(Integer productId, Integer stock) {
        try {
            Product product = productsMapper.findById(productId);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            if (stock < 0) {
                return Result.error("库存不能为负数");
            }
            
            productsMapper.updateStock(productId, stock);
            log.info("更新商品库存成功：productId={}, stock={}", productId, stock);
            return Result.success();
        } catch (Exception e) {
            log.error("更新商品库存失败", e);
            return Result.error("更新商品库存失败：" + e.getMessage());
        }
    }
}


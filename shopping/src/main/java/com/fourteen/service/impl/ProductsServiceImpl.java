package com.fourteen.service.impl;

import com.fourteen.mapper.ProductsMapper;
import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.service.ProductsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品服务实现类
 * 负责商品的分页查询、搜索和详情查询
 */
@Slf4j
@Service
public class ProductsServiceImpl implements ProductsService {
    
    @Autowired
    private ProductsMapper productsMapper;

    /**
     * 分页查询商品列表
     * 支持价格区间筛选、关键词搜索、排序等功能
     * 
     * @param param 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<Product> page(ProductsQueryParam param) {
        // 参数校验和默认值设置
        if (param == null) {
            param = new ProductsQueryParam();
        }
        validateAndSetDefaults(param);
        
        try {
            log.info("开始分页查询商品，参数：page={}, pageSize={}, name={}, minPrice={}, maxPrice={}, sort={}, categoryId={}",
                    param.getPage(), param.getPageSize(), param.getName(), 
                    param.getMinPrice(), param.getMaxPrice(), param.getSort(), param.getCategoryId());
            
            // 启动分页插件（必须在查询之前调用）
            PageHelper.startPage(param.getPage(), param.getPageSize());
            
            // 执行查询
            List<Product> productList = productsMapper.list(param);
            
            // 获取分页信息
            Page<Product> pageInfo = null;
            long total = 0;
            
            if (productList instanceof Page) {
                pageInfo = (Page<Product>) productList;
                total = pageInfo.getTotal();
            } else {
                // 如果PageHelper没有生效，返回空结果
                log.warn("PageHelper未生效，返回空结果");
                return new PageResult<>(0L, new ArrayList<>());
            }
            
            log.info("商品分页查询完成，总记录数：{}，当前页：{}，每页大小：{}，返回{}条", 
                    total, pageInfo.getPageNum(), pageInfo.getPageSize(), productList.size());
            
            // 返回分页结果
            return new PageResult<>(total, productList);
            
        } catch (Exception e) {
            log.error("商品分页查询失败", e);
            // 不抛出异常，返回空结果
            return new PageResult<>(0L, new ArrayList<>());
        } finally {
            // 清理分页参数，防止影响后续查询
            PageHelper.clearPage();
        }
    }

    /**
     * 根据商品ID查询商品详情
     * 
     * @param productId 商品ID
     * @return 商品信息，如果不存在返回null
     */
    @Override
    public Product getProductById(Integer productId) {
        if (productId == null || productId <= 0) {
            log.warn("查询商品详情失败：商品ID无效，productId={}", productId);
            return null;
        }
        
        try {
            log.info("查询商品详情，productId={}", productId);
            Product product = productsMapper.findById(productId);
            
            if (product == null) {
                log.warn("商品不存在，productId={}", productId);
            } else {
                log.info("成功查询商品详情，productId={}, name={}", productId, product.getName());
            }
            
            return product;
            
        } catch (Exception e) {
            log.error("查询商品详情失败，productId={}", productId, e);
            throw new RuntimeException("查询商品详情失败：" + e.getMessage(), e);
        }
    }

    /**
     * 校验查询参数并设置默认值
     * 
     * @param param 查询参数
     */
    private void validateAndSetDefaults(ProductsQueryParam param) {
        // 页码校验和设置默认值
        if (param.getPage() == null || param.getPage() < 1) {
            param.setPage(1);
        }
        
        // 每页数量校验和设置默认值
        if (param.getPageSize() == null || param.getPageSize() < 1) {
            param.setPageSize(12);
        }
        
        // 限制每页最大数量，防止性能问题
        if (param.getPageSize() > 100) {
            param.setPageSize(100);
        }
        
        // 价格区间校验
        if (param.getMinPrice() != null && param.getMinPrice() < 0) {
            param.setMinPrice(null);
        }
        
        if (param.getMaxPrice() != null && param.getMaxPrice() < 0) {
            param.setMaxPrice(null);
        }
        
        // 价格区间逻辑校验
        if (param.getMinPrice() != null && param.getMaxPrice() != null) {
            if (param.getMinPrice() > param.getMaxPrice()) {
                // 交换价格
                Integer temp = param.getMinPrice();
                param.setMinPrice(param.getMaxPrice());
                param.setMaxPrice(temp);
            }
        }
        
        // 商品名称关键词处理：去除首尾空格
        if (param.getName() != null && !param.getName().trim().isEmpty()) {
            String name = param.getName().trim();
            // 限制关键词长度，防止SQL注入和性能问题
            if (name.length() > 50) {
                name = name.substring(0, 50);
            }
            param.setName(name);
        }
        
        // 排序方式校验
        if (param.getSort() == null || param.getSort().trim().isEmpty()) {
            param.setSort("default");
        } else {
            String sort = param.getSort().trim().toLowerCase();
            // 只允许特定的排序方式
            if (!"default".equals(sort) && !"price_asc".equals(sort) && !"price_desc".equals(sort)) {
                param.setSort("default");
            } else {
                param.setSort(sort);
            }
        }
        
        // 分类ID校验
        if (param.getCategoryId() != null && param.getCategoryId() < 0) {
            param.setCategoryId(null);
        }
    }
}

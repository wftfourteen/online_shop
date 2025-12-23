package com.fourteen.controller;

import com.fourteen.pojo.PageResult;
import com.fourteen.pojo.Product;
import com.fourteen.pojo.ProductsQueryParam;
import com.fourteen.pojo.Result;
import com.fourteen.service.ProductsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsService productsService;

    /**
     * 分页查询商品列表
     * 支持价格区间筛选、排序和分页
     */
    @GetMapping
    public Result page(@RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false, defaultValue = "12") Integer pageSize,
                       @RequestParam(required = false) Double minPrice,
                       @RequestParam(required = false) Double maxPrice,
                       @RequestParam(required = false, defaultValue = "default") String sort,
                       @RequestParam(required = false) Integer categoryId,
                       @RequestParam(required = false) String name) {
        try {
            // 构建查询参数对象
            ProductsQueryParam param = new ProductsQueryParam();
            param.setPage(page != null && page > 0 ? page : 1);
            param.setPageSize(pageSize != null && pageSize > 0 ? (pageSize > 100 ? 100 : pageSize) : 12);
            param.setSort(sort != null ? sort : "default");
            
            // 处理价格参数（Double转Integer，取整数部分）
            if (minPrice != null && minPrice >= 0) {
                param.setMinPrice(minPrice.intValue());
            }
            if (maxPrice != null && maxPrice >= 0) {
                param.setMaxPrice(maxPrice.intValue());
            }
            
            // 处理分类ID
            if (categoryId != null && categoryId > 0) {
                param.setCategoryId(categoryId);
            }
            
            // 处理商品名称关键词
            if (name != null && !name.trim().isEmpty()) {
                param.setName(name.trim());
            }
            
            log.info("分页查询商品列表，参数：page={}, pageSize={}, minPrice={}, maxPrice={}, sort={}, categoryId={}, name={}",
                    param.getPage(), param.getPageSize(), param.getMinPrice(), param.getMaxPrice(), 
                    param.getSort(), param.getCategoryId(), param.getName());
            
            // 执行分页查询
            PageResult<Product> pageResult = productsService.page(param);
            
            // 转换返回格式
            List<Map<String, Object>> productList = convertToProductListDto(pageResult.getRows());
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", productList);
            data.put("total", pageResult.getTotal() != null ? pageResult.getTotal() : 0L);
            data.put("totalPage", calculateTotalPages(pageResult.getTotal(), param.getPageSize()));
            data.put("currentPage", param.getPage());
            
            log.info("分页查询成功，返回{}条记录，总计{}条", productList.size(), pageResult.getTotal());
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("分页查询商品列表失败", e);
            return Result.error("查询商品列表失败，请稍后重试");
        }
    }
    
    /**
     * 搜索商品
     * 根据关键词搜索商品名称
     */
    @GetMapping("/search")
    public Result searchProducts(@RequestParam String keyword,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "30") Integer pageSize) {
        try {
            log.info("搜索商品，关键词：{}，页码：{}，每页大小：{}", keyword, page, pageSize);
            
            // 验证关键词
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("搜索关键词不能为空");
            }
            
            // Service层会处理关键词长度限制（最大50字符）
            String trimmedKeyword = keyword.trim();
            if (trimmedKeyword.length() > 50) {
                return Result.error("搜索关键词长度不能超过50个字符");
            }
            
            // 构建查询参数
            ProductsQueryParam param = new ProductsQueryParam();
            param.setPage(page);
            param.setPageSize(pageSize);
            param.setName(trimmedKeyword);
            
            // 执行查询
            PageResult<Product> pageResult = productsService.page(param);
            
            // 转换返回格式
            List<Map<String, Object>> productList = convertToProductListDto(pageResult.getRows());
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("list", productList);
            data.put("total", pageResult.getTotal());
            data.put("totalPage", calculateTotalPages(pageResult.getTotal(), pageSize));
            data.put("currentPage", page);
            data.put("keyword", trimmedKeyword);
            
            log.info("搜索成功，关键词：{}，找到{}条记录", trimmedKeyword, productList.size());
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("搜索商品失败，关键词：{}", keyword, e);
            return Result.error("搜索失败，请稍后重试");
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{productId}")
    public Result getProductDetail(@PathVariable Integer productId) {
        try {
            log.info("查询商品详情，productId={}", productId);
            
            // 参数校验
            if (productId == null || productId <= 0) {
                return Result.error("商品ID无效");
            }
            
            Product product = productsService.getProductById(productId);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            // 转换返回格式，符合接口文档要求
            Map<String, Object> data = convertToProductDetailDto(product);
            
            log.info("成功查询商品详情，productId={}", productId);
            return Result.success(data);
            
        } catch (Exception e) {
            log.error("查询商品详情失败，productId={}", productId, e);
            return Result.error("查询商品详情失败，请稍后重试");
        }
    }
    
    /**
     * 将Product列表转换为DTO格式
     */
    private List<Map<String, Object>> convertToProductListDto(List<Product> products) {
        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> item = new HashMap<>();
            item.put("productId", product.getProductId());
            item.put("mainImage", product.getMainImage());
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("stockStatus", product.getStock()); // 接口文档要求是stockStatus
            productList.add(item);
        }
        return productList;
    }
    
    /**
     * 将Product转换为详情DTO格式
     */
    private Map<String, Object> convertToProductDetailDto(Product product) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId", product.getProductId());
        data.put("name", product.getName());
        data.put("price", product.getPrice());
        data.put("stock", product.getStock());
        
        // 处理图片列表
        List<String> images = parseProductImages(product);
        data.put("images", images);
        data.put("description", product.getDescription());
        
        return data;
    }
    
    /**
     * 解析商品图片列表
     * 支持JSON数组格式和逗号分隔格式
     */
    private List<String> parseProductImages(Product product) {
        List<String> images = new ArrayList<>();
        
        // 添加主图
        if (product.getMainImage() != null && !product.getMainImage().isEmpty()) {
            images.add(product.getMainImage());
        }
        
        // 处理详情图片
        if (product.getDetailImages() != null && !product.getDetailImages().trim().isEmpty()) {
            String detailImages = product.getDetailImages().trim();
            
            if (detailImages.startsWith("[") && detailImages.endsWith("]")) {
                // JSON数组格式，简单解析（去掉方括号和引号）
                detailImages = detailImages.substring(1, detailImages.length() - 1);
                String[] imageArray = detailImages.split(",");
                for (String img : imageArray) {
                    String trimmedImg = img.trim().replace("\"", "").replace("'", "");
                    if (!trimmedImg.isEmpty()) {
                        images.add(trimmedImg);
                    }
                }
            } else {
                // 逗号分隔格式
                String[] imageArray = detailImages.split(",");
                for (String img : imageArray) {
                    String trimmedImg = img.trim();
                    if (!trimmedImg.isEmpty()) {
                        images.add(trimmedImg);
                    }
                }
            }
        }
        
        return images;
    }
    
    /**
     * 计算总页数
     */
    private int calculateTotalPages(Long total, Integer pageSize) {
        if (total == null || total == 0 || pageSize == null || pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }
}


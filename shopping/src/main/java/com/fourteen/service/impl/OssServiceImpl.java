package com.fourteen.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.fourteen.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OssServiceImpl implements OssService {
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;
    
    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;
    
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
    
    @Value("${aliyun.oss.url-prefix}")
    private String urlPrefix;
    
    private OSS getOssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        OSS ossClient = null;
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("文件不能为空");
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("文件名无效");
            }
            
            String extension = "";
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex).toLowerCase();
            }
            
            // 验证文件类型
            if (!extension.equals(".jpg") && !extension.equals(".jpeg") && 
                !extension.equals(".png") && !extension.equals(".webp")) {
                throw new IllegalArgumentException("不支持的图片格式，仅支持 JPG/PNG/WEBP");
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("文件大小不能超过10MB");
            }
            
            // 生成唯一文件名
            String fileName = folder + UUID.randomUUID().toString() + extension;
            
            // 获取OSS客户端
            ossClient = getOssClient();
            
            // 上传文件
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream);
            ossClient.putObject(putObjectRequest);
            
            // 构建文件URL（直接使用配置的URL前缀，确保格式正确）
            String fileUrl;
            if (urlPrefix.endsWith("/")) {
                fileUrl = urlPrefix + fileName;
            } else {
                fileUrl = urlPrefix + "/" + fileName;
            }
            
            log.info("文件上传成功：{}", fileUrl);
            return fileUrl;
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    
    @Override
    public List<String> uploadFiles(MultipartFile[] files, String folder) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String url = uploadFile(file, folder);
                    urls.add(url);
                } catch (Exception e) {
                    log.error("批量上传文件失败：{}", file.getOriginalFilename(), e);
                }
            }
        }
        return urls;
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        OSS ossClient = null;
        try {
            // 从完整URL中提取文件路径
            // 格式：https://bucket-name.oss-cn-region.aliyuncs.com/folder/filename
            String prefix = urlPrefix.endsWith("/") ? urlPrefix : urlPrefix + "/";
            String objectName = fileUrl.replace(prefix, "");
            
            ossClient = getOssClient();
            ossClient.deleteObject(bucketName, objectName);
            
            log.info("文件删除成功：{}", fileUrl);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败：{}", fileUrl, e);
            return false;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}


package com.fourteen.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OssService {
    /**
     * 上传单个文件到OSS
     * @param file 文件
     * @param folder 文件夹路径（如：avatars/, products/）
     * @return 文件的完整URL
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * 上传多个文件到OSS
     * @param files 文件数组
     * @param folder 文件夹路径
     * @return 文件URL列表
     */
    List<String> uploadFiles(MultipartFile[] files, String folder);
    
    /**
     * 删除OSS中的文件
     * @param fileUrl 文件的完整URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
}


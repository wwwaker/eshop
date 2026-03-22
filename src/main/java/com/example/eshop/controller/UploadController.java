package com.example.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class UploadController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images";

    @PostMapping("/admin/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "{\"error\": \"文件为空\"}";
        }

        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        
        // 验证文件类型
        List<String> allowedTypes = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        if (!allowedTypes.contains(suffix.toLowerCase())) {
            return "{\"error\": \"不支持的文件类型\"}";
        }

        // 生成更有意义的文件名：时间戳_原始文件名_随机字符串
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalName = fileName.substring(0, fileName.lastIndexOf("."));
        // 移除原始文件名中的特殊字符，只保留字母和数字
        String cleanOriginalName = originalName.replaceAll("[^a-zA-Z0-9]", "");
        // 限制原始文件名长度
        if (cleanOriginalName.length() > 20) {
            cleanOriginalName = cleanOriginalName.substring(0, 20);
        }
        String randomStr = UUID.randomUUID().toString().substring(0, 4);
        String newFileName = timestamp + "_" + cleanOriginalName + "_" + randomStr + suffix;

        // 使用绝对路径
        String uploadDirPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (!created) {
                return "{\"error\": \"创建上传目录失败\"}";
            }
        }

        try {
            File dest = new File(uploadDirPath + File.separator + newFileName);
            file.transferTo(dest);
            return "{\"url\": \"/images/" + newFileName + "\"}";
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"上传失败：" + e.getMessage() + "\"}";
        }
    }
}

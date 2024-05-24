package com.capstone.emodi.utils;


import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {
    public static String saveImage(MultipartFile image, String uploadDir) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        // 이미지 파일 이름 생성
        String originalFilename = image.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        // 이미지 파일 저장 경로 설정
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 이미지 파일 저장
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(image.getInputStream(), filePath);

        // 저장된 이미지 파일 경로 반환
        return uploadDir + "/" + uniqueFilename;
    }
}
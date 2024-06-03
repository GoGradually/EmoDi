package com.capstone.emodi.utils;


import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUploadUtil {
    public static String saveImage(byte[] imageBytes, String uploadDir) throws IOException {

        String contentType = getImageFormat(imageBytes);
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            default -> throw new IllegalArgumentException("지원되지 않는 이미지 형식입니다.");
        };

        // 이미지 파일 이름 생성
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        String fullPath = uploadDir + "/" + uniqueFilename;

        Path path = Paths.get(fullPath);
        Files.write(path, imageBytes);
        return uniqueFilename;
    }
    private static String getImageFormat(byte[] imageBytes) throws IOException {
        String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(imageBytes));
        return contentType != null ? contentType : "";
    }
}
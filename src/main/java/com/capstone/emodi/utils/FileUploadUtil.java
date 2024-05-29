package com.capstone.emodi.utils;


import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUploadUtil {
    public static String saveImage(MultipartFile image, String uploadDir) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        // 이미지 파일 이름 생성
        String originalFilename = image.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;

        String FullPath = uploadDir + "/" + uniqueFilename;

        image.transferTo(new File(FullPath));
        return uniqueFilename;
    }
}
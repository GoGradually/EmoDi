package com.capstone.emodi.web.image;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Value("${postImage.dir}")
    private String postImageDir;
    @Value("${profileImage.dir}")
    private String profileImageDir;
    @Value("${privatePostImage.dir}")
    private String privatePostImageDir;

    @GetMapping("/profile/{filename}")
    public ResponseEntity<Resource> downloadProfileImage(@PathVariable String filename) throws
            IOException {
        String mimeType = Files.probeContentType(Paths.get(profileImageDir, filename));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new UrlResource("file:" +profileImageDir + "/" + filename));
    }
    @GetMapping("/post/{filename}")
    public ResponseEntity<Resource> downloadPostImage(@PathVariable String filename) throws
            IOException {
        String mimeType = Files.probeContentType(Paths.get(postImageDir, filename));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new UrlResource("file:" +postImageDir + "/" + filename));
    }

    @GetMapping("/privatePost/{filename}")
    public ResponseEntity<Resource> downloadPrivatePostImage(@PathVariable String filename) throws
            IOException {
        String mimeType = Files.probeContentType(Paths.get(privatePostImageDir, filename));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .body(new UrlResource("file:" +privatePostImageDir + "/" + filename));
    }
}
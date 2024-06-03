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
    public ResponseEntity<Hyperlink> downloadProfileImage(@PathVariable String filename) throws
            IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Hyperlink("https://emo-di.com/profileImages/"+ filename));
    }
    @GetMapping("/post/{filename}")
    public ResponseEntity<Hyperlink> downloadPostImage(@PathVariable String filename) throws
            IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Hyperlink("https://emo-di.com/fileStorage/"+ filename));
    }

    @GetMapping("/privatePost/{filename}")
    public ResponseEntity<Hyperlink> downloadPrivatePostImage(@PathVariable String filename) throws
            IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Hyperlink("https://emo-di.com/fileStorage/"+ filename));
    }
    public static class Hyperlink{
        private String imageUrl;

        public Hyperlink(String url){
            this.imageUrl = url;
        }
    }
}
package com.capstone.emodi.web.image;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

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
    public Resource downloadProfileImage(@PathVariable String filename) throws
            MalformedURLException {
        return new UrlResource("file:" +profileImageDir + "/" + filename);
    }
    @GetMapping("/post/{filename}")
    public Resource downloadPostImage(@PathVariable String filename) throws
            MalformedURLException {
        return new UrlResource("file:" + postImageDir + "/" +  filename);
    }

    @GetMapping("/privatePost/{filename}")
    public Resource downloadPrivatePostImage(@PathVariable String filename) throws
            MalformedURLException {
        return new UrlResource("file:" + privatePostImageDir + "/" + filename);
    }
}
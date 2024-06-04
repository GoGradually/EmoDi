package com.capstone.emodi;

import com.capstone.emodi.web.interceptor.LogInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("**") // 필요에 따라 도메인 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 기본 프로필 이미지 경로 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/home/ubuntu/storage/images/");
        registry.addResourceHandler("/profileImages/**")
                .addResourceLocations("file:/home/ubuntu/storage/profileImages/");
        registry.addResourceHandler("/privateImages/**")
                .addResourceLocations("file:/home/ubuntu/storage/privateImages/");


    }
}
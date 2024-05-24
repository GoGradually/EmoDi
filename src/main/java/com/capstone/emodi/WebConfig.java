package com.capstone.emodi;

import com.capstone.emodi.web.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
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
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile-images/**")
                .addResourceLocations("file:profile-images/");
        // 기본 프로필 이미지 경로 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");


        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
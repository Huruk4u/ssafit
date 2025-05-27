package com.example.ssafit.config;

import com.example.ssafit.interceptor.SuspensionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String PROFILE_IMAGE_PATH = "C:/temp/profile/";
    private static final String BACKGROUND_IMAGE_PATH = "C:/temp/background/";
    private static final String BADGES_IMAGE_PATH = "C:/Users/SSAFY/Desktop/pjt/ssafit/SSAFIT/src/main/resources/static/images/badges/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:///" + PROFILE_IMAGE_PATH);
        registry.addResourceHandler("/images/background/**")
                .addResourceLocations("file:///" + BACKGROUND_IMAGE_PATH);

        // 뱃지 이미지를 위한 리소스 핸들러 추가
        registry.addResourceHandler("/images/badges/**")
                .addResourceLocations("file:///" + BADGES_IMAGE_PATH);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SuspensionInterceptor())
                .addPathPatterns("/api_article/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
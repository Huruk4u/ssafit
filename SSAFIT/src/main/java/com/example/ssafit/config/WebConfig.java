package com.example.ssafit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 기본 설정 1. Image Source 매핑 경로 설정
 * 이미지가 열리지 않을 경우 확인할 것:
 * 파일이 실제로 ResourceLocations에 존재하는가?
 * Spring Boot 서버 로그에 정적 요청 로그가 찍히는가?
 * 파일 이름이 대소문자까지 정확히 일치하는지?
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // front level에서 정적 리소스에 접근을 시도할 떄, 접근을 허용합니다.
    private static final String PROFILE_IMAGE_PATH = "C:/temp/profile/";

    private static final String BACKGROUND_IMAGE_PATH = "C:/temp/background/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:///" + PROFILE_IMAGE_PATH)
                .addResourceLocations("file:///" + BACKGROUND_IMAGE_PATH);
    }
}

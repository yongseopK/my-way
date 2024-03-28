package com.healthmap.myway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")    // 어떤 요청 URL을 허용할지
                .allowedOrigins("https://*") // 어떤 클라이언트를 허용할지;
                .allowedMethods("*")    // 어떤 요청방식을 허용할지
                .allowedHeaders("*")    // 어떤 헤더를 허용할지
                .allowCredentials(true) // 쿠키 전달을 허용할지
                .maxAge(3600)           // 허용시간에 대한 캐싱 설정
        ;
    }
}

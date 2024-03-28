package com.healthmap.myway.config;

import com.healthmap.myway.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
                cors()
                .and()
                .csrf().disable()
                .httpBasic().disable()
                // 세선 인증은 더 이상 사용하지 않겠다.
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 어떤 요청에서는 인증을 하고, 어떤 요청에서는 인증을 안할건지
                .authorizeRequests()// 어떤 요청에서 인증을 할 것이냐.
                .antMatchers("/api/auth/load-profile").authenticated()
                .antMatchers("/", "/member/**").permitAll() // 이 요청은 인증을 안해도 됨.
                .anyRequest().authenticated()   // 나머지 요청은 모두 인증ㄱㄱ
        ;

        // 토큰인증 필터 연결
        http.addFilterAfter(jwtAuthFilter, CorsFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

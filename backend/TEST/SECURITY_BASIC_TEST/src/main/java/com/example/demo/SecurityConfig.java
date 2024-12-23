package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보안 설정
                .formLogin(form -> form // 로그인 설정
                        .loginPage("https://k11e104.p.ssafy.io/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("userId")
                        .passwordParameter("password"))
                .logout(Customizer.withDefaults()) // 로그아웃 설정
                .authorizeHttpRequests(req -> req
                        .anyRequest().authenticated()) // 모든 요청에 대해 인증 필요
                .sessionManagement(auth -> auth
                        .sessionFixation().changeSessionId() // 세션 고정 공격 방지
                        .maximumSessions(1).maxSessionsPreventsLogin(true)); // 세션 동시 사용 방지
        return http.build();
    }

    // 사용자 정보 설정
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("test")
                .password(passwordEncoder().encode("test"))
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // 패스워드 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

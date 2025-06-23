package com.lca2.gateway_service.security.config;

import com.lca2.gateway_service.security.filter.JwtAuthenticationFilter;
import com.lca2.gateway_service.security.jwt.JwtTokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenValidator jwtTokenValidator;

    @Bean
    public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/**")
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenValidator),
                        UsernamePasswordAuthenticationFilter.class
                )

                .authorizeHttpRequests(registry -> registry
                                .anyRequest().permitAll()

//                        // 회원가입, 로그인
//                        .requestMatchers("/user/signup", "/user/login").permitAll()
//                        .requestMatchers("/api/questions").permitAll()
//                        .requestMatchers("/profile").permitAll()
//                        .requestMatchers("/questions/**").permitAll()
//
//                        // 게시글, 댓글 관련 GET 요청
//                        .requestMatchers(HttpMethod.GET, "/comm").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/comm/{postId}").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/comm/{postId}/comments").permitAll()
//
//                        .anyRequest().authenticated()

                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

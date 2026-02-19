package com.notificationbridge.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AUTH_HEADER = "Authorization";
    private static final String API_KEY_PREFIX = "ApiKey ";

    @Value("${app.security.api-key:}")
    private String configuredApiKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(new ApiKeyAuthFilter(configuredApiKey), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @RequiredArgsConstructor
    private static class ApiKeyAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {

        private final String configuredApiKey;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {
            if (SecurityContextHolder.getContext().getAuthentication() != null
                    && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            String apiKey = request.getHeader(API_KEY_HEADER);
            if (!StringUtils.hasText(apiKey) && StringUtils.hasText(request.getHeader(AUTH_HEADER))) {
                String auth = request.getHeader(AUTH_HEADER);
                if (auth.startsWith(API_KEY_PREFIX)) {
                    apiKey = auth.substring(API_KEY_PREFIX.length()).trim();
                }
            }

            if (StringUtils.hasText(configuredApiKey) && configuredApiKey.equals(apiKey)) {
                var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "api-key", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);
                return;
            }

            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write("{\"message\":\"Missing or invalid API key\"}");
                return;
            }

            filterChain.doFilter(request, response);
        }
    }
}

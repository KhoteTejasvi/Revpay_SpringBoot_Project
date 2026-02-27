package com.revpayproject.revpay.config;

import com.revpayproject.revpay.security.JwtAuthFilter;
import com.revpayproject.revpay.security.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ✅ ADD THIS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // ✅ Swagger Whitelist
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ✅ Auth APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // ✅ Admin
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // PERSONAL users can pay invoices
                        .requestMatchers(HttpMethod.POST, "/api/invoice/*/pay")
                        .hasRole("USER")

                        // BUSINESS users manage invoices
                        .requestMatchers(HttpMethod.POST, "/api/invoice/create")
                        .hasRole("BUSINESS")

                        .requestMatchers(HttpMethod.POST, "/api/invoice/*/send")
                        .hasRole("BUSINESS")

                        .requestMatchers(HttpMethod.POST, "/api/invoice/*/cancel")
                        .hasRole("BUSINESS")

                        .requestMatchers(HttpMethod.POST, "/api/invoice/*/mark-paid-manual")
                        .hasRole("BUSINESS")

                        // Allow both roles to view invoices
                        .requestMatchers("/api/invoice/**")
                        .hasAnyRole("BUSINESS", "PERSONAL")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(rateLimitFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

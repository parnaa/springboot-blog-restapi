package com.example.demoBlog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demoBlog.filter.JwtAuthenticationFilter;
import com.example.demoBlog.util.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Lazy
    private JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/blogs/public/**").permitAll()
                .requestMatchers("/api/chat/rooms").permitAll()  // Allow public chat room access
                .requestMatchers("/ws/**").permitAll()  // Allow WebSocket connections
                .requestMatchers("/app/**").permitAll()  // Allow WebSocket app endpoints
                .requestMatchers("/topic/**").permitAll()  // Allow WebSocket topics
                .requestMatchers("/websocket-demo.html").permitAll()  // Allow access to demo page
                .requestMatchers("/static/**").permitAll()  // Allow static resources
                .requestMatchers("/*.html").permitAll()  // Allow HTML files
                .requestMatchers("/*.css").permitAll()  // Allow CSS files
                .requestMatchers("/*.js").permitAll()  // Allow JavaScript files
                .requestMatchers("/test**").permitAll()  // Allow test endpoints
                .requestMatchers("/api/blogs").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/blogs/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/chat/rooms/**").hasAnyRole("USER", "ADMIN")  // Protected chat operations
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (your frontend URL)
        configuration.addAllowedOrigin("http://localhost:3000");  // React default
        configuration.addAllowedOrigin("http://localhost:5173");  // Vite default
        configuration.addAllowedOrigin("http://localhost:8080");  // Backend (for testing)
        configuration.addAllowedOrigin("http://127.0.0.1:5173");  // Alternative localhost
        
        // Allow all HTTP methods
        configuration.addAllowedMethod("*");
        
        // Allow all headers
        configuration.addAllowedHeader("*");
        
        // Allow credentials (for JWT tokens)
        configuration.setAllowCredentials(true);
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
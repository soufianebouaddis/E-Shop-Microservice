package com.e_shop.auth_service.config;

import com.e_shop.auth_service.constant.Constant;
import com.e_shop.auth_service.jwt.service.JwtService;
import com.e_shop.auth_service.service.impl.UserDetailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;

import java.security.interfaces.RSAPublicKey;

import java.security.interfaces.RSAPublicKey;

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class AuthServiceSecurityConfig {
    private final JwtService jwtService;
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailService userDetailsService;
    private final AuthenticationEntry authenticationEntry;

    @Value("${jwt-keys.public_key}")
    private String public_key;
    public AuthServiceSecurityConfig(JwtService jwtService, JwtAuthFilter jwtAuthFilter, UserDetailService userDetailsService, AuthenticationEntry authenticationEntry) {
        this.jwtService = jwtService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.authenticationEntry = authenticationEntry;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(csrf -> {
                    csrf
                            .csrfTokenRepository(cookieCsrfTokenRepository())
                            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                            .ignoringRequestMatchers(
                                    "/api/v1/auth/login-cookie",
                                    "/api/v1/auth/register",
                                    "/api/v1/auth/refresh-token"
                            );
                }).addFilterBefore(new CsrfCookieFilter(), CsrfFilter.class)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntry))
                .oauth2ResourceServer(authorize -> authorize.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/v1/auth/register").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/v1/auth/refreshToken").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/v1/auth/login").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/v1/auth/login-cookie").permitAll())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/v1/auth/public-key").permitAll())
                .authorizeHttpRequests(
                        authorize -> authorize.requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**").permitAll())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/api/v1/users/logout")
                        .permitAll()
                        .logoutSuccessHandler((req, res, auth) -> {
                            String origin = req.getHeader("Origin");
                            if ("http://localhost:3000".equals(origin) || "http://localhost:80".equals(origin)) {
                                res.setHeader("Access-Control-Allow-Origin", origin);
                            }
                            res.setHeader("Access-Control-Allow-Credentials", "true");
                            res.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies(
                                Constant.REFRESHTOKEN_COOKIE_NAME,
                                "JSESSIONID",
                                Constant.COOKIE_NAME)
                        .clearAuthentication(true)
                )
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .build();

    }
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService);
        return authenticationProvider;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    JwtDecoder jwtDecoder() throws Exception {
        final RSAPublicKey rsaPublicKey = (RSAPublicKey) this.jwtService.loadPublicKey(public_key);
        final NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
        return decoder;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler(){
        return new SecurityContextLogoutHandler();
    }
    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/");
        return repository;
    }

}

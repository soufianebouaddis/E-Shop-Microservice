package com.e_shop.auth_service.config;
import com.e_shop.auth_service.constant.Constant;
import com.e_shop.auth_service.jwt.service.JwtService;
import com.e_shop.auth_service.service.impl.UserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
@Configuration
public class JwtAuthFilter extends OncePerRequestFilter{
    private JwtService jwtService;

    UserDetailService userDetailService;

    public JwtAuthFilter(JwtService jwtService, UserDetailService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/api/v1/users/refreshToken")) {
            logger.warn("Bypassing the refresh token endpoint");
            filterChain.doFilter(request, response);
            return;
        }
        String token = null;
        String username = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(Constant.COOKIE_NAME)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (jwtService.isTokenExpired(token)) {
                logger.error("Jwt expired");
            }
            username = jwtService.extractUsername(token);
            if (username != null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            logger.error("Session expired");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"JWT token expired\", \"message\": \"" + e.getMessage() + "\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}


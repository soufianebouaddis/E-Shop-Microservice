package com.e_shop.auth_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            String token = csrfToken.getToken();
            Cookie csrfCookie = new Cookie("XSRF-TOKEN", token);
            csrfCookie.setPath("/");
            csrfCookie.setHttpOnly(false);
            csrfCookie.setSecure(false);
            response.addCookie(csrfCookie);
        }
        filterChain.doFilter(request, response);
    }
}

package com.e_shop.auth_service.jwt.impl;

import com.e_shop.auth_service.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;
@Service
public class JwtServiceImpl implements JwtService {
    private static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 hours
    private PrivateKey privateKey;
    private PublicKey publicKey;
    @Value("${jwt-keys.private_key}")
    private String privk;
    @Value("${jwt-keys.public_key}")
    private String pubk;
    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey(privk);
            this.publicKey = loadPublicKey(pubk);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load RSA keys", e);
        }
    }
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
    }
    @Override
    public Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }
    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String generateToken(String username,List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username,roles);
    }
    @Override
    public String createToken(Map<String, Object> claims, String username, List<String> roles) {
        claims.put("roles", roles); // Add the roles claim

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuer("http://localhost:8180")
                .setAudience("product-service")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(privateKey)
                .compact();
    }
    @Override
    public PrivateKey loadPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }
    @Override
    public PublicKey loadPublicKey(String key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}

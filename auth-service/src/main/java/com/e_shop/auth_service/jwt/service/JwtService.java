package com.e_shop.auth_service.jwt.service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import io.jsonwebtoken.Claims;

public interface JwtService {
    public String extractUsername(String token) ;
    public Date extractExpiration(String token);
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) ;
    public Claims extractAllClaims(String token);
    public Boolean isTokenExpired(String token) ;
    public Boolean validateToken(String token, UserDetails userDetails) ;
    public String generateToken(String username,List<String> roles) ;

    public String createToken(Map<String, Object> claims, String username, List<String> roles);

    public PrivateKey loadPrivateKey(String key) throws Exception ;

    public PublicKey loadPublicKey(String key) throws Exception ;
}

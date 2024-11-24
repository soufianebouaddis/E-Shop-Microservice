package com.e_shop.auth_service.jwt.service;

import com.e_shop.auth_service.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    public RefreshToken createRefreshToken(String username) ;

    public Optional<RefreshToken> findByToken(String token);

    public RefreshToken verifyExpiration(RefreshToken token);
    public boolean validateRefreshToken(String token) ;

}

package com.e_shop.auth_service.jwt.impl;

import com.e_shop.auth_service.jwt.service.RefreshTokenService;
import com.e_shop.auth_service.model.RefreshToken;
import com.e_shop.auth_service.repository.RefreshTokenRepository;
import com.e_shop.auth_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }


    @Override
    public RefreshToken createRefreshToken(String username) {
        Optional<RefreshToken> optionalToken = refreshTokenRepository.findRefreshTokenByUsername(username);
        if (optionalToken.isPresent()) {
            RefreshToken existingToken = optionalToken.get();
            if (existingToken.getExpiryDate().isAfter(Instant.now())) {
                existingToken.setExpiryDate(Instant.now().plusMillis(28800000));
                return refreshTokenRepository.save(existingToken);
            } else {
                refreshTokenRepository.delete(existingToken);
            }
        }
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(userRepository.findUserByUsername(username))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(28800000)) // Adds 8 hours
                .build();
        return refreshTokenRepository.save(newRefreshToken);
    }



    @Override
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
    @Override
    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }
    @Override
    public boolean validateRefreshToken(String token) {
        try{
            Optional<RefreshToken> optionalToken = refreshTokenRepository.findByToken(token);
            return optionalToken.isPresent() && optionalToken.get().getExpiryDate().isAfter(Instant.now());
        }catch (RuntimeException ex){
            throw new RuntimeException("Refresh token not valide");
        }

    }
}

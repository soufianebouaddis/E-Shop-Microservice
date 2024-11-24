package com.e_shop.auth_service.repository;

import com.e_shop.auth_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {
    Optional<RefreshToken> findByToken(String token);
    @Query("SELECT rf FROM RefreshToken rf WHERE rf.user.username = :username")
    Optional<RefreshToken> findRefreshTokenByUsername(@Param("username") String username);
}
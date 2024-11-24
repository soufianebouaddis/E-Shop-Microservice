package com.e_shop.auth_service.service;

import com.e_shop.auth_service.model.RefreshToken;
import com.e_shop.auth_service.model.User;
import com.e_shop.auth_service.request.LoginRequest;
import com.e_shop.auth_service.response.UserInfo;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface UserService {
    public User update(int id, User o);

    public User findById(int id);

    public User add(User o);

    public User delete(int id);

    public List<User> readAll();

    public UserInfo getUser();
    public Map<String , ResponseCookie> authenticate(LoginRequest authRequestDTO);

    public Authentication authenticateUser(LoginRequest authRequestDTO);

    public ResponseCookie createAccessTokenCookie(String accessToken);
    public ResponseCookie createRefreshTokenCookie(String token);
    public RefreshToken getTokenOfUserByUsername(String username);
}

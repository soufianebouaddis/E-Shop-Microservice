package com.e_shop.auth_service.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.e_shop.auth_service.constant.Constant;
import com.e_shop.auth_service.exception.UserNotFound;
import com.e_shop.auth_service.jwt.service.JwtService;
import com.e_shop.auth_service.jwt.service.RefreshTokenService;
import com.e_shop.auth_service.mapper.impl.UserMapper;
import com.e_shop.auth_service.model.RefreshToken;
import com.e_shop.auth_service.model.Role;
import com.e_shop.auth_service.model.User;
import com.e_shop.auth_service.repository.RefreshTokenRepository;
import com.e_shop.auth_service.repository.UserRepository;
import com.e_shop.auth_service.request.LoginRequest;
import com.e_shop.auth_service.response.UserInfo;
import com.e_shop.auth_service.service.UserService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserDetailService detailService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final RefreshTokenService jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImpl(UserDetailService detailService, UserRepository userRepository, UserMapper userMapper, JwtService jwtService, RefreshTokenService jwtUtils, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository) {
        this.detailService = detailService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public User update(int id, User o) {
        return userRepository.findById(id).map((user) -> {
            user.setNom(o.getNom());
            user.setPrenom(o.getPrenom());
            user.setEmail(o.getEmail());
            user.setUsername(o.getUsername());
            user.setPassword(o.getPassword());
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFound("User not found with ID : "
                + id));
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found with ID: " + id));
    }

    @Override
    public User add(User o) {
        return userRepository.save(o);
    }
    @Override
    public User delete(int id) {
        User temp = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found with ID: " +
                        id));
        userRepository.deleteById(id);
        return temp;
    }
    @Override
    public List<User> readAll() {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new UserNotFound("No users found");
        }
        return userList;
    }
    public UserInfo getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();
        Optional<User> user = Optional.ofNullable(userRepository.findUserByUsername(usernameFromAccessToken));
        if(user.isPresent()){
            return userMapper.mapFromAuthUserToUserInfoResponse(user.get());
        }
        return new UserInfo();
    }
    public Map<String, ResponseCookie> authenticate(LoginRequest authRequestDTO) {
        Authentication authentication = authenticateUser(authRequestDTO);
        if (authentication.isAuthenticated()) {
            User user = detailService.loadUserByUsername(authRequestDTO.getUsername());
            List<String> roles = user.getRoles().stream().map(Role::getRoleName).toList();
            String accessToken = jwtService.generateToken(user.getUsername(), roles);
            RefreshToken refreshToken = jwtUtils.createRefreshToken(authRequestDTO.getUsername());
            ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken.getToken());
            ResponseCookie accessTokenCookie = createAccessTokenCookie(accessToken);

            Map<String, ResponseCookie> cookies = new HashMap<>();
            cookies.put(Constant.COOKIE_NAME, accessTokenCookie);
            cookies.put(Constant.REFRESHTOKEN_COOKIE_NAME, refreshTokenCookie);
            return cookies;
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    public Authentication authenticateUser(LoginRequest authRequestDTO) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword())
        );
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from(Constant.COOKIE_NAME,
                        accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                //.sameSite("None")
                .maxAge(7200) // 2H
                .build();
    }
    public ResponseCookie createRefreshTokenCookie(String token){
        return ResponseCookie.from("Spring-refreshToken",
                        token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(86400) // 1 day
                .build();
    }
    public RefreshToken getTokenOfUserByUsername(String username){
        return refreshTokenRepository.findRefreshTokenByUsername(username).orElseThrow(
                ()-> new UserNotFound("User not found")
        );
    }
}

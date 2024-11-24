package com.e_shop.auth_service.controller;

import com.e_shop.auth_service.common.CookieExtraction;
import com.e_shop.auth_service.common.CustomResponseEntity;
import com.e_shop.auth_service.constant.Constant;
import com.e_shop.auth_service.dto.UserAuthDTO;
import com.e_shop.auth_service.exception.UserNotFound;
import com.e_shop.auth_service.jwt.service.JwtService;
import com.e_shop.auth_service.jwt.service.RefreshTokenService;
import com.e_shop.auth_service.mapper.impl.UserMapper;
import com.e_shop.auth_service.model.RefreshToken;
import com.e_shop.auth_service.model.Role;
import com.e_shop.auth_service.model.User;
import com.e_shop.auth_service.repository.RoleRepository;
import com.e_shop.auth_service.repository.UserRepository;
import com.e_shop.auth_service.request.LoginRequest;
import com.e_shop.auth_service.response.JwtResponse;
import com.e_shop.auth_service.service.UserService;
import com.e_shop.auth_service.service.impl.UserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    @Value("${jwt-keys.public_key}")
    private String publicKey;
    private final UserService userService;

    private final UserMapper userMapper;

    private final RefreshTokenService jwtUtils;

    private final UserDetailService detailService;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextLogoutHandler logoutHandler;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, UserMapper userMapper, RefreshTokenService jwtUtils, UserDetailService detailService, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtService jwtService, AuthenticationManager authenticationManager, SecurityContextLogoutHandler logoutHandler) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.detailService = detailService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.logoutHandler = logoutHandler;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable int userId) throws UserNotFound {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public CustomResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> userList = userService.readAll();
            return new CustomResponseEntity<>(true, userList, HttpStatus.OK);
        } catch (UserNotFound ex) {
            return new CustomResponseEntity<>(false, Collections.emptyList(), ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error during getting users");
        }
    }

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CustomResponseEntity<User> saveUser(@RequestBody @Valid UserAuthDTO userDto) {
        try {
            return new CustomResponseEntity<>(
                    true,
                    userService.add(userMapper.mapToEntity(userDto)),
                    HttpStatus.CREATED);
        } catch (Exception ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during adding new user");

        }
    }

    @PutMapping("/update/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CustomResponseEntity<?> updateUser(@PathVariable int userId, @RequestBody @Valid User updatedUser) {
        try {
            return new CustomResponseEntity<>(
                    true,
                    userService.update(userId, updatedUser),
                    HttpStatus.OK);
        } catch (UserNotFound ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during updating user");
        }
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public CustomResponseEntity<?> deleteUser(@PathVariable int userId) {
        try {

            return new CustomResponseEntity<>(true, userService.delete(userId), HttpStatus.OK);
        } catch (UserNotFound ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during deleting user");
        }
    }

    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        User user = detailService.loadUserByUsername(loginRequest.getUsername());
        if (encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            List<String> roles = user.getRoles().stream().map(Role::getRoleName).toList();
            String accessToken = jwtService.generateToken(user.getUsername(), roles);
            String refreshToken = jwtUtils.createRefreshToken(user.getUsername()).getToken();
            return ResponseEntity.ok().body(new JwtResponse(accessToken, refreshToken));
        }
        return ResponseEntity.badRequest().body("Wrong credentials, try again");
    }



    @PostMapping("/login-cookie")
    public CustomResponseEntity<?> authenticate(@RequestBody LoginRequest authRequestDTO,
                                                HttpServletResponse response) {
        try {
            logger.info("Authentication method inside User controller has been called");
            Map<String, ResponseCookie> cookieMap = userService.authenticate(authRequestDTO);
            response.addHeader(HttpHeaders.SET_COOKIE, cookieMap.get(Constant.COOKIE_NAME).toString());
            response.addHeader(HttpHeaders.SET_COOKIE, cookieMap.get(Constant.REFRESHTOKEN_COOKIE_NAME).toString());
            return new CustomResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during authenticating check credentials");
        }
    }

    @PostMapping("/logout")
    public CustomResponseEntity<?> logout(Authentication authentication, HttpServletRequest request,
                                          HttpServletResponse response) {
        try {
            logger.info("user logout successfully");
            this.logoutHandler.logout(request, response, authentication);
            return new CustomResponseEntity<>(true, HttpStatus.OK, "User Logout Successfully");
        } catch (Exception ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during logout");
        }
    }

    @PostMapping("/register")
    public CustomResponseEntity<?> register(@RequestBody @Valid UserAuthDTO dto) {
        Optional<User> user = userRepository.findUserByUsernameOrEmail(dto.getUsername(), dto.getEmail());
        if (user.isPresent()) {
            throw new EntityNotFoundException("founded");
        }
        Role role = roleRepository.findRoleByRoleName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        User newUser = User.builder()
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .password(encoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .email(dto.getEmail())
                .roles(new HashSet<>(Collections.singleton(role)))
                .build();
        userService.add(newUser);
        role.getUsers().add(newUser);
        roleRepository.save(role);
        return new CustomResponseEntity<>(true, newUser, HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        logger.warn("Calling refresh Token endpoint");
        String refreshTokenString = CookieExtraction.extractTokenFromRequest(request, Constant.REFRESHTOKEN_COOKIE_NAME);
        if (refreshTokenString != null && jwtUtils.validateRefreshToken(refreshTokenString)) {
            logger.warn("Token valid");
            Optional<RefreshToken> optionalRefreshToken = jwtUtils.findByToken(refreshTokenString);
            if (optionalRefreshToken.isPresent()) {
                RefreshToken refreshToken = optionalRefreshToken.get();
                try {
                    RefreshToken verifiedRefreshToken = jwtUtils.verifyExpiration(refreshToken);
                    String username = verifiedRefreshToken.getUser().getUsername();
                    List<String> roles = userService.getUser().getRoles().stream().map(Role::getRoleName).toList();
                    String newAccessToken = jwtService.generateToken(username,roles);
                    ResponseCookie accessTokenCookie = userService.createAccessTokenCookie(newAccessToken);
                    logger.info("Access token generated and set");
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                            .body(new CustomResponseEntity<>(true, HttpStatus.OK));
                } catch (RuntimeException e) {
                    logger.error("Refresh token has expired. Please log in again.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Refresh token has expired. Please log in again.");
                }
            } else {
                logger.error("Refresh Token is not in DB..!!");
                throw new RuntimeException("Refresh Token is not in DB..!!");
            }
        } else {
            logger.error("Invalid Refresh Token");
            throw new InvalidBearerTokenException("Invalid Refresh Token");
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public CustomResponseEntity<?> userProfile() {
        try {
            return new CustomResponseEntity<>(true, userService.getUser(), HttpStatus.OK);
        } catch (UserNotFound ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.NOT_FOUND,
                    "Error user not authenticate or not found in our system");
        } catch (Exception ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during getting user info");
        }
    }

    @GetMapping("/isAuthenticated")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public CustomResponseEntity<?> checkAuthentication() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return new CustomResponseEntity<>(true, authentication != null && authentication.isAuthenticated(),
                    HttpStatus.OK);
        } catch (UserNotFound ex) {
            return new CustomResponseEntity<>(false, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during verify user authentication");
        }
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAdmin() {
        return ResponseEntity.ok(new StringBuilder("Hey Mr Admin"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok(new StringBuilder("Hey Mr User"));
    }

    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {
        return ResponseEntity.ok(publicKey);
    }
}

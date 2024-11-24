package com.e_shop.auth_service.service.impl;

import com.e_shop.auth_service.exception.UserNotFound;
import com.e_shop.auth_service.model.User;
import com.e_shop.auth_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User loadUserByUsername(String username)  {
        User user = userRepository.findUserByUsername(username);
        if(user != null){
            return user;
        }
        throw new UserNotFound("User not found");
    }

}
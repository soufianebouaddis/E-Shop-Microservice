package com.e_shop.auth_service.repository;

import com.e_shop.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    public User findUserByUsername(String Username);
    public Optional<User> findUserByUsernameOrEmail(String Username, String Email);
}

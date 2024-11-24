package com.e_shop.auth_service.response;

import com.e_shop.auth_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String nom;
    private String prenom;
    private String username;
    private Set<Role> roles;
}
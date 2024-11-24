package com.e_shop.auth_service.mapper.impl;

import com.e_shop.auth_service.dto.UserAuthDTO;
import com.e_shop.auth_service.dto.UserDto;
import com.e_shop.auth_service.mapper.service.MapperService;
import com.e_shop.auth_service.model.User;
import com.e_shop.auth_service.response.UserInfo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements MapperService<User, UserAuthDTO> {
    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Override
    public User mapToEntity(UserAuthDTO destination) {
        return this.modelMapper.map(destination, User.class);
    }

    @Override
    public UserAuthDTO mapFromEntity(User source) {
        return this.modelMapper.map(source, UserAuthDTO.class);
    }

    public User mapFromUserResponseInfoToAuthUser(UserInfo userInfoResponse){
        return modelMapper.map(userInfoResponse, User.class);
    }
    public UserInfo mapFromAuthUserToUserInfoResponse(User user){
        return modelMapper.map(user,UserInfo.class);
    }
}

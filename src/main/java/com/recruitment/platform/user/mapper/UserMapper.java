package com.recruitment.platform.user.mapper;

import com.recruitment.platform.user.dto.RegisterUserRequest;
import com.recruitment.platform.user.dto.UserResponse;
import com.recruitment.platform.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    public User toEntity(RegisterUserRequest request){
        User user = new User();
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setEmail(request.getEmail());
        return user;
    }


}

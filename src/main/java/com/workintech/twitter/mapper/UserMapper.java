package com.workintech.twitter.mapper;

import com.workintech.twitter.dto.request.RegisterRequest;
import com.workintech.twitter.dto.response.UserResponse;
import com.workintech.twitter.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setBio(request.getBio());
        return user;
    }
    
    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getBio(),
            user.getCreatedAt()
        );
    }
}

package com.recruitment.platform.user.service;

import com.recruitment.platform.user.entity.User;
import com.recruitment.platform.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public User createuser(User user){
        return userRepository.save(user);
    }
}

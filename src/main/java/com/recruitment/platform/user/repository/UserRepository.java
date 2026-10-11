package com.recruitment.platform.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.recruitment.platform.user.entity.User;
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
}

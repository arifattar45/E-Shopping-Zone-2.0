package com.user.service.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.service.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByResetToken(String resetToken);
}

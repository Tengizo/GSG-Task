package com.gsg.task.gsgtask.service;


import com.gsg.task.gsgtask.api.dto.UserDTO;
import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String username) {
        return this.userRepository.getUserByUsername(username).orElse(null);
    }
    public Optional<User> getLoggedInUser() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::getUserByUsername);
    }
    public User getUser(Long id) {
        return this.userRepository.getUserById(id).orElse(null);
    }

    public void addUser(UserDTO dto) {
        User user = dto.toUser();
        if (this.userRepository.getUserByUsername(user.getUsername()).isPresent()) {
            throw new AppException(ExceptionType.USERNAME_IS_USED);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.userRepository.addUser(user);
    }

    public void updateUser(UserDTO dto) {
        Optional<User> userOp = this.userRepository.getUserById(dto.getId());
        if (userOp.isEmpty()) {
            throw new AppException(ExceptionType.USER_NOT_FOUND);
        }
        User user = userOp.get();
        user.setJobInterval(dto.getJobInterval());
        this.userRepository.updateUser(user);
    }

}

package com.gsg.task.gsgtask.service;


import com.gsg.task.gsgtask.api.dto.UserDTO;
import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import com.gsg.task.gsgtask.scheduler.TaskRunner;
import com.gsg.task.gsgtask.security.SecurityUtils;
import com.gsg.task.helper.CountryHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskRunner taskRunner;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TaskRunner taskRunner) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskRunner = taskRunner;
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
        this.addUser(dto.toUser());
    }

    public void addUser(User user) {
        if (this.userRepository.getUserByUsername(user.getUsername()).isPresent()) {
            throw new AppException(ExceptionType.USERNAME_IS_USED);
        }
        if (!CountryHelper.countryIsCorrect(user.getCountry())) {
            throw new AppException(ExceptionType.INVALID_COUNTRY);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User u = this.userRepository.addUser(user);
        this.taskRunner.addTask(u);

    }

    public void updateUser(UserDTO dto) {
        Optional<User> userOp = this.userRepository.getUserById(dto.getId());
        if (userOp.isEmpty()) {
            throw new AppException(ExceptionType.USER_NOT_FOUND);
        }
        if (!CountryHelper.countryIsCorrect(dto.getCountry())) {
            throw new AppException(ExceptionType.INVALID_COUNTRY);
        }
        User user = userOp.get();
        user.setJobInterval(dto.getJobInterval());
        user.setCountry(dto.getCountry());
        this.userRepository.updateUser(user);
    }

}

package com.gsg.task.gsgtask.security;

import com.gsg.task.gsgtask.persistance.entity.User;
import com.gsg.task.gsgtask.persistance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        log.debug("Authenticating {}", username);

        Optional<User> user = userRepository.getUserByUsername(username);
        return user.map(this::createSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " was not found in the database"));
    }

    private AppUserDetails createSpringSecurityUser(User user) {
        return new AppUserDetails(
                user.getId(), user.getUsername(),
                user.getPassword(),
                new ArrayList<>());
    }


}

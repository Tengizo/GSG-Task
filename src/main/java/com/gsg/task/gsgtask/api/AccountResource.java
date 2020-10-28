package com.gsg.task.gsgtask.api;

import com.gsg.task.gsgtask.api.dto.JWTToken;
import com.gsg.task.gsgtask.api.dto.LoginDTO;
import com.gsg.task.gsgtask.api.dto.UserDTO;
import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import com.gsg.task.gsgtask.security.SecurityConstants;
import com.gsg.task.gsgtask.security.TokenProvider;
import com.gsg.task.gsgtask.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api/account")
public class AccountResource {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AccountResource(UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * POST  /sign-up : register the user.
     *
     * @param userDTO the managed user View Model
     */
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody UserDTO userDTO) {
        this.userService.addUser(userDTO);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateAccount(@Valid @RequestBody UserDTO userDTO) {
        this.userService.updateUser(userDTO);
    }

    @GetMapping("")
    public UserDTO getAccount() {
        return userService.getLoggedInUser()
                .map(UserDTO::new)
                .orElseThrow(() -> new AppException(ExceptionType.USER_NOT_FOUND));
    }

    @PostMapping("/login")
    public ResponseEntity<JWTToken> authorize(@RequestBody LoginDTO loginDTO) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());

        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SecurityConstants.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

}

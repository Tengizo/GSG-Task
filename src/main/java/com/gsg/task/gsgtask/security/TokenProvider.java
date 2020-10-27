package com.gsg.task.gsgtask.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gsg.task.gsgtask.api.errors.exception.AppException;
import com.gsg.task.gsgtask.api.errors.exception.ExceptionType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String SECRET = "some_secret";
    private static final String ISSUER = "gsg_task";
    private static final String USER_ID = "USER_ID";
    private static final long VALIDITY = 1000 * 60 * 60 * 24;
    private final JWTVerifier verifier;

    public TokenProvider() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        this.verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        ;
    }

    public String createToken(Authentication authentication) {
        Date validity = new Date((new Date()).getTime() + VALIDITY);
        AppUserDetails springSecurityUser = (AppUserDetails) authentication.getPrincipal();
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(springSecurityUser.getUsername())
                    .withExpiresAt(validity)
                    .withClaim(USER_ID, springSecurityUser.getId().toString())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new AppException(ExceptionType.SERVER_SIDE_EXCEPTION);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            AppUserDetails principal = new AppUserDetails(
                    Long.valueOf(claims.get(USER_ID).as(String.class)),
                    jwt.getSubject(), "", new ArrayList<>()
            );
            return new UsernamePasswordAuthenticationToken(principal, token, new ArrayList<>());
        } catch (JWTVerificationException exception) {
            throw new AppException(ExceptionType.INVALID_TOKEN);
        }

    }

    public boolean validateToken(String authToken) {
        try {
            //Reusable verifier instance
            verifier.verify(authToken);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }
}

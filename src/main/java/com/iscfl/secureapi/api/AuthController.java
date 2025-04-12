package com.iscfl.secureapi.api;

import com.iscfl.secureapi.model.*;
import com.iscfl.secureapi.security.AuthenticationProviders;
import com.iscfl.secureapi.utils.AppConstants;
import com.iscfl.secureapi.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserDetailsManager userDetailsManager;
    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    AuthenticationProviders authenticationProviders;

    @RequestMapping(value = "/register", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Token register(@RequestBody @Validated SignUp signupDTO) {
        User user = new User(signupDTO.getUsername(), signupDTO.getPassword(), AppConstants.defaultRole);
        userDetailsManager.createUser(user);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), user.getAuthorities());

        return tokenGenerator.createToken(authentication);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Token login(@RequestBody @Validated Login loginDTO) {
        DaoAuthenticationProvider daoAuthenticationProvider = (DaoAuthenticationProvider)
                authenticationProviders.getProvider(AppConstants.DAO_AUTH_PROVIDER);
        Authentication authentication = daoAuthenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword())
        );


        return tokenGenerator.createToken(authentication);
    }

    @RequestMapping(value = "/token", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Token token(@RequestBody @Validated Token tokenDTO) {
        JwtAuthenticationProvider refreshTokenAuthProvider = (JwtAuthenticationProvider)
                authenticationProviders.getProvider(AppConstants.REFRESHTOKEN_AUTH_PROVIDER);
        Authentication authentication = refreshTokenAuthProvider.authenticate(
                new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken())
        );
        Jwt jwt = (Jwt) authentication.getCredentials(); // manage revocation of refresh token

        return tokenGenerator.createToken(authentication);
    }
}

package org.AFM.rssbridge.controller;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.dto.request.LoginRequest;
import org.AFM.rssbridge.dto.request.SignupRequest;
import org.AFM.rssbridge.dto.response.JwtResponse;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.user.model.User;
import org.AFM.rssbridge.user.service.RSSUserDetailService;

import org.AFM.rssbridge.uitl.JwtRequestFilter;
import org.AFM.rssbridge.uitl.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {
    private final RSSUserDetailService userDetailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest
    ){
        LOGGER.warn("IIN IS: " + loginRequest.getIin() + " PASSWORD IS " + loginRequest.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIin(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during authentication: " + e.getMessage());
        }

        UserDetails userDetails = userDetailService.loadUserByUsername(loginRequest.getIin());
        String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok().body(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest signupRequest
    ){
        try {
            User newUser = new User();
            newUser.setIin(signupRequest.getIin());
            newUser.setName(signupRequest.getName());
            newUser.setSurname(signupRequest.getSurname());
            newUser.setFathername(signupRequest.getFathername());
            newUser.setPassword(signupRequest.getPassword());

            userDetailService.saveUser(newUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the user: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: " + e.getMessage());
        }
    }

}

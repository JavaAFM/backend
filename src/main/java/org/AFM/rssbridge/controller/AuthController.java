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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

        User userDetails = (User) userDetailService.loadUserByUsername(loginRequest.getIin());
        String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);


        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccess(accessToken);
        jwtResponse.setRefresh(refreshToken);
        jwtResponse.setIin(userDetails.getIin());
        jwtResponse.setFio(userDetails.getName() + " " + userDetails.getSurname() + " " + userDetails.getFathername());
        jwtResponse.setRole(userDetails.getRole().getName());

        return ResponseEntity.ok().body(jwtResponse);
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid refresh token format");
        }

        refreshToken = refreshToken.substring(7);

        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }

        String username = jwtTokenUtil.extractUsername(refreshToken);
        User userDetails = (User) userDetailService.loadUserByUsername(username);
        String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setAccess(newAccessToken);
        jwtResponse.setRefresh(refreshToken);
        jwtResponse.setIin(userDetails.getIin());
        jwtResponse.setFio(userDetails.getName() + " " + userDetails.getSurname() + " " + userDetails.getFathername());
        jwtResponse.setRole(userDetails.getRole().getName());

        return ResponseEntity.ok(jwtResponse);
    }

}

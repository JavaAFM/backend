package org.AFM.rssbridge.user.service.impl;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.user.repository.RoleRepository;
import org.AFM.rssbridge.user.repository.UserRepository;
import org.AFM.rssbridge.user.model.Role;
import org.AFM.rssbridge.user.model.User;
import org.AFM.rssbridge.user.service.RSSUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class RSSUserDetailsServiceImpl implements RSSUserDetailService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserLogServiceImpl userLogService;
    @Override
    public UserDetails loadUserByUsername(String iin) throws UsernameNotFoundException {
        User user = userRepository.findByIin(iin)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with IIN: " + iin));

        userLogService.logAction(user.getId(), "LOGIN", "User logged in using IIN: " + iin);
        return user;
    }

    @Transactional("usersTransactionManager")
    public void saveUser(User user) throws NotFoundException {
        Role defaultRole = roleRepository.findByName("user").orElseThrow(()-> new NotFoundException("Default role not found."));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(defaultRole);

        userRepository.save(user);
    }/*
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username)
        );

        userLogService.logAction(user.getId(), "LOGIN", "User logged in successfully");

        return user;
    }
*/

    public void logout(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        User user = userOptional.orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: " + userId)
        );

        userLogService.logAction(user.getId(), "LOGOUT", "User logged out successfully");
    }

    @Transactional("usersTransactionManager")
    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

}

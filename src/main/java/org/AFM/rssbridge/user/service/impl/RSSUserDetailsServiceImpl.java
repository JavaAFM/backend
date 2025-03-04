package org.AFM.rssbridge.user.service.impl;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.exception.NotFoundException;
import org.AFM.rssbridge.user.repository.RoleRepository;
import org.AFM.rssbridge.user.repository.UserRepository;
import org.AFM.rssbridge.user.model.Role;
import org.AFM.rssbridge.user.model.User;
import org.AFM.rssbridge.user.service.RSSUserDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class RSSUserDetailsServiceImpl implements RSSUserDetailService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String iin) throws UsernameNotFoundException {
        return userRepository.findByIin(iin).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional("usersTransactionManager")
    public void saveUser(User user) throws NotFoundException {
        Role defaultRole = roleRepository.findByName("user").orElseThrow(()-> new NotFoundException("Default role not found."));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(defaultRole);

        userRepository.save(user);
    }

    @Transactional("usersTransactionManager")
    public Page<User> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable);
    }

}

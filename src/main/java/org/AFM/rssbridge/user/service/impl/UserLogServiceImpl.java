package org.AFM.rssbridge.user.service.impl;

import org.AFM.rssbridge.user.model.User;
import org.AFM.rssbridge.user.model.UserLog;
import org.AFM.rssbridge.user.repository.UserLogRepository;
import org.AFM.rssbridge.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserLogServiceImpl {

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Log action based on Long userId.
     */
    public void logAction(Long userId, String action, String details) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            UserLog log = new UserLog();
            log.setUser(user);
            log.setAction(action);
            log.setDetails(details);

            userLogRepository.save(log);
        } else {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }


    public List<UserLog> getLogsByUser(Long userId) {
        return userLogRepository.findByUser_Id(userId);
    }


    public List<UserLog> getLogsByAction(String action) {
        return userLogRepository.findByAction(action);
    }


    public List<UserLog> getLogsByUserAndAction(Long userId, String action) {
        return userLogRepository.findByUserAndAction(userId, action);
    }
}

package org.AFM.rssbridge.user.repository;

import org.AFM.rssbridge.user.model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Long> {

    List<UserLog> findByUser_Id(Long userId);

    List<UserLog> findByAction(String action);

    List<UserLog> findByUserAndAction(Long userId, String action);
}

package org.AFM.rssbridge.user.repository;

import org.AFM.rssbridge.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIin(String iin);

    Page<User> findAll(Pageable pageable);
}

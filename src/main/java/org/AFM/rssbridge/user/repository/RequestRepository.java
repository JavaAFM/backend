package org.AFM.rssbridge.user.repository;

import org.AFM.rssbridge.user.model.SourceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<SourceRequest, Long> {
    List<SourceRequest> getAddSourceRequestsByUserIin(String iin);
    Optional<SourceRequest> getSourceRequestById(Long id);
}

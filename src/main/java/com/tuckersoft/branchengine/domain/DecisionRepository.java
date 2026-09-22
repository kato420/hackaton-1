package com.tuckersoft.branchengine.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DecisionRepository extends JpaRepository<Decision, Long> {
    
    @Query("SELECT d FROM Decision d WHERE " +
           "(:branchType IS NULL OR d.branchType = :branchType) AND " +
           "(:impactLevel IS NULL OR d.impactLevel = :impactLevel) AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:playthroughId IS NULL OR d.playthrough.id = :playthroughId) AND " +
           "(:ownerEmail IS NULL OR d.playthrough.user.email = :ownerEmail)")
    Page<Decision> findWithFilters(
            @Param("branchType") String branchType,
            @Param("impactLevel") String impactLevel,
            @Param("status") String status,
            @Param("playthroughId") Long playthroughId,
            @Param("ownerEmail") String ownerEmail,
            Pageable pageable);
            
    List<Decision> findAllByPlaythroughIdAndResolvedNodeCodeIsNotNullOrderByCreatedAtAsc(Long playthroughId);
}

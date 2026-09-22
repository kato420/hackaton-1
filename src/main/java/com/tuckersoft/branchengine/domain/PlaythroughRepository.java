package com.tuckersoft.branchengine.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaythroughRepository extends JpaRepository<Playthrough, Long> {
    boolean existsByPlayerTag(String playerTag);
    List<Playthrough> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<Playthrough> findAllByOrderByCreatedAtDesc();
}

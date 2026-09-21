package com.tuckersoft.branchengine.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RealityLogRepository extends JpaRepository<RealityLog, Long> {
    List<RealityLog> findAllByDecisionId(Long decisionId);
}

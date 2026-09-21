package com.tuckersoft.branchengine.web;

import com.tuckersoft.branchengine.domain.Decision;
import com.tuckersoft.branchengine.domain.DecisionRepository;
import com.tuckersoft.branchengine.domain.RealityLog;
import com.tuckersoft.branchengine.domain.RealityLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/decisions/{id}/reality-logs")
public class RealityLogController {
    
    private final RealityLogRepository logRepository;
    private final DecisionRepository decisionRepository;
    
    public RealityLogController(RealityLogRepository logRepository, DecisionRepository decisionRepository) {
        this.logRepository = logRepository;
        this.decisionRepository = decisionRepository;
    }
    
    @GetMapping
    public ResponseEntity<?> getLogs(@PathVariable Long id, Authentication authentication) {
        Decision d = decisionRepository.findById(id).orElse(null);
        if (d == null) return ResponseEntity.notFound().build();
        
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (!"ROLE_ADMIN".equals(role) && !d.getPlaythrough().getUser().getEmail().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso");
        }
        
        return ResponseEntity.ok(logRepository.findAllByDecisionId(id));
    }
}

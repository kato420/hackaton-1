package com.tuckersoft.branchengine.web;

import com.tuckersoft.branchengine.service.DecisionService;
import com.tuckersoft.branchengine.web.dto.DecisionRequest;
import com.tuckersoft.branchengine.web.dto.DecisionResponse;
import com.tuckersoft.branchengine.web.dto.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    public ResponseEntity<DecisionResponse> createDecision(
            @Valid @RequestBody DecisionRequest request, 
            @RequestHeader(value = "X-Bandersnatch-Simulate", required = false) String simulate,
            Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        DecisionResponse response = decisionService.processDecision(request, authentication.getName(), role, simulate);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DecisionResponse>> getDecisions(
            @RequestParam(required = false) String branchType,
            @RequestParam(required = false) String impactLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long playthroughId,
            Authentication authentication,
            Pageable pageable) {
            
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        Page<DecisionResponse> page = decisionService.getDecisions(branchType, impactLevel, status, playthroughId, authentication.getName(), role, pageable);
        
        PaginatedResponse<DecisionResponse> response = new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DecisionResponse> getDecision(@PathVariable Long id, Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        DecisionResponse response = decisionService.getDecisionById(id, authentication.getName(), role);
        return ResponseEntity.ok(response);
    }
}

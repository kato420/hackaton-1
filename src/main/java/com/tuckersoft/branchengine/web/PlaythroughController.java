package com.tuckersoft.branchengine.web;

import com.tuckersoft.branchengine.security.CustomUserDetails;
import com.tuckersoft.branchengine.service.PlaythroughService;
import com.tuckersoft.branchengine.web.dto.PathResponse;
import com.tuckersoft.branchengine.web.dto.PlaythroughRequest;
import com.tuckersoft.branchengine.web.dto.PlaythroughResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playthroughs")
public class PlaythroughController {

    private final PlaythroughService playthroughService;

    public PlaythroughController(PlaythroughService playthroughService) {
        this.playthroughService = playthroughService;
    }

    @PostMapping
    public ResponseEntity<PlaythroughResponse> createPlaythrough(@Valid @RequestBody PlaythroughRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playthroughService.createPlaythrough(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<PlaythroughResponse>> getPlaythroughs(Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(playthroughService.getPlaythroughs(authentication.getName(), role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaythroughResponse> getPlaythroughById(@PathVariable Long id, Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(playthroughService.getPlaythroughById(id, authentication.getName(), role));
    }

    @GetMapping("/{id}/path")
    public ResponseEntity<PathResponse> getPath(@PathVariable Long id, Authentication authentication) {
        String role = getRole(authentication);
        return ResponseEntity.ok(playthroughService.getPlaythroughPath(id, authentication.getName(), role));
    }
    
    private String getRole(Authentication authentication) {
        return authentication.getAuthorities().iterator().next().getAuthority();
    }
}

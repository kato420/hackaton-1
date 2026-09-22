package com.tuckersoft.branchengine.web;

import com.tuckersoft.branchengine.domain.User;
import com.tuckersoft.branchengine.domain.UserRepository;
import com.tuckersoft.branchengine.web.dto.RoleUpdateRequest;
import com.tuckersoft.branchengine.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return ResponseEntity.ok(mapToResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request, Authentication authentication) {
        User targetUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String newRole = request.getRole();
        if (!"ROLE_USER".equals(newRole) && !"ROLE_ADMIN".equals(newRole)) {
            throw new BadRequestException("Rol invalido");
        }
        
        if (targetUser.getEmail().equals(authentication.getName())) {
            throw new BadRequestException("No puedes cambiar tu propio rol");
        }
        
        targetUser.setRole(newRole);
        userRepository.save(targetUser);
        
        return ResponseEntity.ok(mapToResponse(targetUser));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole(), user.getCreatedAt());
    }
}

package com.tuckersoft.branchengine.web;

import com.tuckersoft.branchengine.domain.User;
import com.tuckersoft.branchengine.domain.UserRepository;
import com.tuckersoft.branchengine.security.JwtUtil;
import com.tuckersoft.branchengine.web.dto.AuthResponse;
import com.tuckersoft.branchengine.web.dto.LoginRequest;
import com.tuckersoft.branchengine.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email ya registrado");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setRole("ROLE_USER");
        user.setCreatedAt(Instant.now());
        
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        AuthResponse response = new AuthResponse(token, "Bearer", user.getEmail(), user.getDisplayName(), user.getRole());
                
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        AuthResponse response = new AuthResponse(token, "Bearer", user.getEmail(), user.getDisplayName(), user.getRole());
                
        return ResponseEntity.ok(response);
    }
}

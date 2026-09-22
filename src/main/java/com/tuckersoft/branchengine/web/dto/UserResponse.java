package com.tuckersoft.branchengine.web.dto;

import java.time.Instant;

public class UserResponse {
    private Long id;
    private String email;
    private String displayName;
    private String role;
    private Instant createdAt;

    public UserResponse(Long id, String email, String displayName, String role, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }
}

package com.tuckersoft.branchengine.web.dto;

public class AuthResponse {
    private String token;
    private String type;
    private String email;
    private String displayName;
    private String role;

    public AuthResponse(String token, String type, String email, String displayName, String role) {
        this.token = token;
        this.type = type;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
    }
    public String getToken() { return token; }
    public String getType() { return type; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }
}

package com.tuckersoft.branchengine.web;

public class ErrorResponse {
    private String error;
    private String message;
    private String timestamp;
    private String path;

    public ErrorResponse(String error, String message, String timestamp, String path) {
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getPath() { return path; }
}

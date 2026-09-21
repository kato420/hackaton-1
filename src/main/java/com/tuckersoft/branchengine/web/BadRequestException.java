package com.tuckersoft.branchengine.web;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}

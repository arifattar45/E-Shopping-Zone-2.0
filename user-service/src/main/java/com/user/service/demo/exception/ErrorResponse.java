package com.user.service.demo.exception;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private LocalDateTime time;

    public ErrorResponse(String message, LocalDateTime time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() { return message; }
    public LocalDateTime getTime() { return time; }
}

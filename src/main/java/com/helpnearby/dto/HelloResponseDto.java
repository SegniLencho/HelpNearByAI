package com.helpnearby.dto;

public class HelloResponseDto {

    private String message;
    private String timestamp;

    public HelloResponseDto() {
    }

    public HelloResponseDto(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}

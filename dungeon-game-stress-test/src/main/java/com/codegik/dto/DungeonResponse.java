package com.codegik.dto;

public class DungeonResponse {

    private int result;
    private String message;

    public DungeonResponse() {
    }

    public DungeonResponse(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

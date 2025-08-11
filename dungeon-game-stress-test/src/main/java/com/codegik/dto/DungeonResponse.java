package com.codegik.dto;

public class DungeonResponse {

    private int minimumHP;
    private String message;

    public DungeonResponse() {
    }

    public DungeonResponse(int minimumHP, String message) {
        this.minimumHP = minimumHP;
        this.message = message;
    }

    public int getMinimumHP() {
        return minimumHP;
    }

    public void setMinimumHP(int minimumHP) {
        this.minimumHP = minimumHP;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.codegik.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

public class DungeonRequest {

    @NotNull(message = "Dungeon cannot be null")
    @NotEmpty(message = "Dungeon cannot be empty")
    private int[][] dungeon;

    public DungeonRequest() {
    }

    public DungeonRequest(int[][] dungeon) {
        this.dungeon = dungeon;
    }

    public int[][] getDungeon() {
        return dungeon;
    }

    public void setDungeon(int[][] dungeon) {
        this.dungeon = dungeon;
    }
}

package com.codegik.controller;

import com.codegik.game.DungeonGame;
import com.codegik.dto.DungeonRequest;
import com.codegik.dto.DungeonResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/dungeon")
@CrossOrigin(origins = "*")
public class DungeonGameController {

    private final DungeonGame dungeonGame;

    public DungeonGameController() {
        this.dungeonGame = new DungeonGame();
    }

    @PostMapping("/calculate")
    public ResponseEntity<DungeonResponse> calculateMinimumHP(@Valid @RequestBody DungeonRequest request) {
        try {
            int minimumHP = dungeonGame.calculateMinimumHP(request.getDungeon());
            DungeonResponse response = new DungeonResponse(minimumHP, "Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DungeonResponse errorResponse = new DungeonResponse(0, "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Dungeon Game API is running");
    }
}

package com.codegik.controller;

import com.codegik.dto.DungeonRequest;
import com.codegik.dto.DungeonResponse;
import com.codegik.entity.DungeonResult;
import com.codegik.service.DungeonGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dungeon")
@CrossOrigin(origins = "*")
public class DungeonGameController {

    private final DungeonGameService dungeonGameService;

    @Autowired
    public DungeonGameController(DungeonGameService dungeonGameService) {
        this.dungeonGameService = dungeonGameService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<DungeonResponse> calculateMinimumHP(@Valid @RequestBody DungeonRequest request) {
        try {
            DungeonResult result = dungeonGameService.calculateAndSave(request.getDungeon());
            DungeonResponse response = new DungeonResponse(result.getResult(), "Success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DungeonResponse errorResponse = new DungeonResponse(0, "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<DungeonResult>> getAllResults() {
        List<DungeonResult> results = dungeonGameService.getAllResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<DungeonResult> getResultById(@PathVariable Long id) {
        Optional<DungeonResult> result = dungeonGameService.getResultById(id);
        return result.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/results/dimensions/{rows}/{columns}")
    public ResponseEntity<List<DungeonResult>> getResultsByDimensions(
            @PathVariable Integer rows, @PathVariable Integer columns) {
        List<DungeonResult> results = dungeonGameService.getResultsByDimensions(rows, columns);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/result/{result}")
    public ResponseEntity<List<DungeonResult>> getResultsByResult(@PathVariable Integer result) {
        List<DungeonResult> results = dungeonGameService.getResultsByResult(result);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/stats/average/{rows}/{columns}")
    public ResponseEntity<Double> getAverageResult(
            @PathVariable Integer rows, @PathVariable Integer columns) {
        Double average = dungeonGameService.getAverageResult(rows, columns);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/stats/count")
    public ResponseEntity<Long> getTotalCount() {
        long count = dungeonGameService.getTotalResultsCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Dungeon Game API is running");
    }
}

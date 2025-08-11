package com.codegik.service;

import com.codegik.entity.DungeonResult;
import com.codegik.game.DungeonGame;
import com.codegik.repository.DungeonResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DungeonGameService {

    private final DungeonGame dungeonGame;
    private final DungeonResultRepository repository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DungeonGameService(DungeonResultRepository repository) {
        this.dungeonGame = new DungeonGame();
        this.repository = repository;
        this.objectMapper = new ObjectMapper();
    }

    public DungeonResult calculateAndSave(int[][] dungeon) {
        try {
            // Calculate minimum HP
            int minimumHP = dungeonGame.calculateMinimumHP(dungeon);

            // Convert dungeon array to JSON string for storage
            String dungeonData = objectMapper.writeValueAsString(dungeon);

            // Create and save result
            DungeonResult result = new DungeonResult(
                dungeonData,
                minimumHP,
                dungeon.length,
                dungeon[0].length
            );

            return repository.save(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize dungeon data", e);
        }
    }

    public List<DungeonResult> getAllResults() {
        return repository.findAllOrderByCreatedAtDesc();
    }

    public Optional<DungeonResult> getResultById(Long id) {
        return repository.findById(id);
    }

    public List<DungeonResult> getResultsByDimensions(int rows, int columns) {
        return repository.findByRowsAndColumns(rows, columns);
    }

    public List<DungeonResult> getResultsByMinimumHP(int minimumHP) {
        return repository.findByMinimumHP(minimumHP);
    }

    public List<DungeonResult> getResultsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return repository.findByCreatedAtBetween(start, end);
    }

    public Double getAverageMinimumHP(int rows, int columns) {
        return repository.findAverageMinimumHPByDimensions(rows, columns);
    }

    public long getTotalResultsCount() {
        return repository.count();
    }
}

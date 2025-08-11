package com.codegik.service;

import com.codegik.entity.DungeonResult;
import com.codegik.game.DungeonGame;
import com.codegik.repository.DungeonResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            int result = dungeonGame.calculateMinimumHP(dungeon);
            String dungeonData = objectMapper.writeValueAsString(dungeon);

            DungeonResult dungeonResult = new DungeonResult(
                dungeonData,
                result,
                dungeon.length,
                dungeon[0].length
            );

            return repository.save(dungeonResult);
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

    public List<DungeonResult> getResultsByResult(int result) {
        return repository.findByResult(result);
    }

    public Double getAverageResult(int rows, int columns) {
        return repository.findAverageResultByDimensions(rows, columns);
    }

    public long getTotalResultsCount() {
        return repository.count();
    }
}

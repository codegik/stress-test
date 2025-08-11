package com.codegik.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("dungeon_results")
public class DungeonResult {

    @Id
    private Long id;

    @Column("dungeon_data")
    private String dungeonData;

    @Column("minimum_hp")
    private Integer minimumHP;

    @Column("rows")
    private Integer rows;

    @Column("columns")
    private Integer columns;

    @Column("created_at")
    private LocalDateTime createdAt;

    public DungeonResult() {
        this.createdAt = LocalDateTime.now();
    }

    public DungeonResult(String dungeonData, Integer minimumHP, Integer rows, Integer columns) {
        this();
        this.dungeonData = dungeonData;
        this.minimumHP = minimumHP;
        this.rows = rows;
        this.columns = columns;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDungeonData() {
        return dungeonData;
    }

    public void setDungeonData(String dungeonData) {
        this.dungeonData = dungeonData;
    }

    public Integer getMinimumHP() {
        return minimumHP;
    }

    public void setMinimumHP(Integer minimumHP) {
        this.minimumHP = minimumHP;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

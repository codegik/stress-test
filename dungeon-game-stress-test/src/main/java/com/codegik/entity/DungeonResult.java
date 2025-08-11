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

    @Column("result")
    private Integer result;

    @Column("rows")
    private Integer rows;

    @Column("columns")
    private Integer columns;

    @Column("created_at")
    private LocalDateTime createdAt;

    public DungeonResult() {
        this.createdAt = LocalDateTime.now();
    }

    public DungeonResult(String dungeonData, Integer result, Integer rows, Integer columns) {
        this();
        this.dungeonData = dungeonData;
        this.result = result;
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

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
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

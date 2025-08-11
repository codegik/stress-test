package com.codegik.repository;

import com.codegik.entity.DungeonResult;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DungeonResultRepository extends CrudRepository<DungeonResult, Long> {

    List<DungeonResult> findByResult(Integer result);

    List<DungeonResult> findByRowsAndColumns(Integer rows, Integer columns);

    @Query("SELECT * FROM dungeon_results ORDER BY created_at DESC")
    List<DungeonResult> findAllOrderByCreatedAtDesc();

    @Query("SELECT AVG(result) FROM dungeon_results WHERE rows = :rows AND columns = :columns")
    Double findAverageResultByDimensions(@Param("rows") Integer rows, @Param("columns") Integer columns);
}

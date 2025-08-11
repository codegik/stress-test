package com.codegik.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DungeonGameTest {

    private DungeonGame dungeonGame;

    @BeforeEach
    void setUp() {
        dungeonGame = new DungeonGame();
    }

    @Test
    @DisplayName("Test basic dungeon scenario with mixed positive and negative values")
    void testBasicDungeon() {
        int[][] dungeon = {
            {-3, 5},
            {1, -4}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(4, result);
    }

    @Test
    @DisplayName("Test classic dungeon game example")
    void testClassicExample() {
        int[][] dungeon = {
            {-3, 5, -2},
            {-1, -2, -4},
            {2, -3, -1}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Test single cell dungeon with negative value")
    void testSingleCellNegative() {
        int[][] dungeon = {{-5}};
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(6, result);
    }

    @Test
    @DisplayName("Test single cell dungeon with positive value")
    void testSingleCellPositive() {
        int[][] dungeon = {{10}};
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Test single row dungeon")
    void testSingleRow() {
        int[][] dungeon = {{-1, -2, 5, -3}};
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(4, result);
    }

    @Test
    @DisplayName("Test single column dungeon")
    void testSingleColumn() {
        int[][] dungeon = {
            {-1},
            {-2},
            {5},
            {-3}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(4, result);
    }

    @Test
    @DisplayName("Test dungeon with all positive values")
    void testAllPositiveValues() {
        int[][] dungeon = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Test dungeon with all negative values")
    void testAllNegativeValues() {
        int[][] dungeon = {
            {-1, -2, -3},
            {-4, -5, -6},
            {-7, -8, -9}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(22, result);
    }

    @Test
    @DisplayName("Test dungeon with zero values")
    void testWithZeroValues() {
        int[][] dungeon = {
            {0, -1, 0},
            {2, 0, -3},
            {0, 1, 0}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Test larger dungeon")
    void testLargerDungeon() {
        int[][] dungeon = {
            {-3, 5, -2, 1},
            {-1, -2, -4, 3},
            {2, -3, -1, -2},
            {1, -1, 2, -3}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertTrue(result > 0);
    }

    @ParameterizedTest
    @MethodSource("provideDungeonTestCases")
    @DisplayName("Parameterized test for various dungeon scenarios")
    void testVariousDungeonScenarios(int[][] dungeon, int expectedMinHP, String description) {
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(expectedMinHP, result, description);
    }

    static Stream<Arguments> provideDungeonTestCases() {
        return Stream.of(
            Arguments.of(
                new int[][]{{-3, 5}},
                4,
                "Simple 1x2 dungeon"
            ),
            Arguments.of(
                new int[][]{{5, -3}},
                1,
                "1x2 dungeon with positive start"
            ),
            Arguments.of(
                new int[][]{
                    {-3},
                    {5}
                },
                4,
                "2x1 dungeon"
            ),
            Arguments.of(
                new int[][]{
                    {100, -100, -100},
                    {-100, -100, -100},
                    {-100, -100, 100}
                },
                201,
                "Extreme values dungeon"
            )
        );
    }

    @Test
    @DisplayName("Test edge case - minimum possible dungeon size")
    void testMinimumDungeonSize() {
        int[][] dungeon = {{0}};
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Test that result is always positive")
    void testResultAlwaysPositive() {
        int[][] dungeon = {
            {-1000, -1000},
            {-1000, -1000}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertTrue(result > 0, "Minimum health should always be at least 1");
    }

    @Test
    @DisplayName("Test optimal path selection")
    void testOptimalPathSelection() {
        int[][] dungeon = {
            {-3, 5},
            {-1, -4}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertEquals(4, result);
    }

    @Test
    @DisplayName("Test symmetrical dungeon")
    void testSymmetricalDungeon() {
        int[][] dungeon = {
            {-2, 1, -2},
            {1, -3, 1},
            {-2, 1, -2}
        };
        int result = dungeonGame.calculateMinimumHP(dungeon);
        assertTrue(result > 0);
    }
}

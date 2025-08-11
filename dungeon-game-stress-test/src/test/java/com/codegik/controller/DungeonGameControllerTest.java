package com.codegik.controller;

import com.codegik.dto.DungeonRequest;
import com.codegik.entity.DungeonResult;
import com.codegik.service.DungeonGameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DungeonGameController.class)
@ActiveProfiles("test")
class DungeonGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DungeonGameService dungeonGameService;

    @Test
    @DisplayName("Health endpoint should return success")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/dungeon/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dungeon Game API is running"));
    }

    @Test
    @DisplayName("Calculate minimum HP and save to database")
    void testCalculateMinimumHPBasic() throws Exception {
        int[][] dungeon = {{-3, 5}, {1, -4}};
        DungeonRequest request = new DungeonRequest(dungeon);

        DungeonResult mockResult = new DungeonResult("[[[-3,5],[1,-4]]]", 4, 2, 2);
        when(dungeonGameService.calculateAndSave(any(int[][].class))).thenReturn(mockResult);

        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minimumHP").value(4))
                .andExpect(jsonPath("$.message").value("Success - Result saved to database"));
    }

    @Test
    @DisplayName("Get all results")
    void testGetAllResults() throws Exception {
        DungeonResult result1 = new DungeonResult("[[[-3,5]]]", 4, 1, 2);
        DungeonResult result2 = new DungeonResult("[[[10]]]", 1, 1, 1);
        List<DungeonResult> results = Arrays.asList(result1, result2);

        when(dungeonGameService.getAllResults()).thenReturn(results);

        mockMvc.perform(get("/api/dungeon/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Get result by ID")
    void testGetResultById() throws Exception {
        DungeonResult result = new DungeonResult("[[[-5]]]", 6, 1, 1);
        when(dungeonGameService.getResultById(1L)).thenReturn(Optional.of(result));

        mockMvc.perform(get("/api/dungeon/results/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minimumHP").value(6));
    }

    @Test
    @DisplayName("Get result by ID - not found")
    void testGetResultByIdNotFound() throws Exception {
        when(dungeonGameService.getResultById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/dungeon/results/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get results by dimensions")
    void testGetResultsByDimensions() throws Exception {
        DungeonResult result = new DungeonResult("[[[-3,5],[1,-4]]]", 4, 2, 2);
        when(dungeonGameService.getResultsByDimensions(2, 2)).thenReturn(Arrays.asList(result));

        mockMvc.perform(get("/api/dungeon/results/dimensions/2/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Get total count")
    void testGetTotalCount() throws Exception {
        when(dungeonGameService.getTotalResultsCount()).thenReturn(42L);

        mockMvc.perform(get("/api/dungeon/stats/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    @Test
    @DisplayName("Should handle invalid request with empty body")
    void testInvalidRequestEmptyBody() throws Exception {
        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void testMalformedJSON() throws Exception {
        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}

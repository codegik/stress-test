package com.codegik.controller;

import com.codegik.dto.DungeonRequest;
import com.codegik.dto.DungeonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DungeonGameController.class)
@ActiveProfiles("test")
class DungeonGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Health endpoint should return success")
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/dungeon/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dungeon Game API is running"));
    }

    @Test
    @DisplayName("Calculate minimum HP for basic dungeon")
    void testCalculateMinimumHPBasic() throws Exception {
        int[][] dungeon = {{-3, 5}, {1, -4}};
        DungeonRequest request = new DungeonRequest(dungeon);

        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minimumHP").value(4))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("Calculate minimum HP for classic example")
    void testCalculateMinimumHPClassic() throws Exception {
        int[][] dungeon = {{-3, 5, -2}, {-1, -2, -4}, {2, -3, -1}};
        DungeonRequest request = new DungeonRequest(dungeon);

        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minimumHP").value(5))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("Calculate minimum HP for single cell dungeon")
    void testCalculateMinimumHPSingleCell() throws Exception {
        int[][] dungeon = {{-5}};
        DungeonRequest request = new DungeonRequest(dungeon);

        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minimumHP").value(6))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    @DisplayName("Calculate minimum HP for positive value dungeon")
    void testCalculateMinimumHPPositive() throws Exception {
        int[][] dungeon = {{10}};
        DungeonRequest request = new DungeonRequest(dungeon);

        mockMvc.perform(post("/api/dungeon/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minimumHP").value(1))
                .andExpect(jsonPath("$.message").value("Success"));
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

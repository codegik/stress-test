package com.codegik.controller;

import com.codegik.dto.DungeonRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DungeonGameControllerTest {

    @LocalServerPort
    private int port;

    private HttpClient httpClient;
    private String baseUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newBuilder().build();
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("Health endpoint should return success")
    void testHealthEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/health"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Dungeon Game API is running", response.body());
    }

    @Test
    @DisplayName("Calculate result and save to database - integration test")
    void testCalculateAndSaveIntegration() throws Exception {
        int[][] dungeon = {{-3, 5}, {1, -4}};
        DungeonRequest dungeonRequest = new DungeonRequest(dungeon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(dungeonRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(4, jsonResponse.get("result").asInt());
        assertEquals("Success - Result saved to database", jsonResponse.get("message").asText());
    }

    @Test
    @DisplayName("Calculate classic example and save to database")
    void testCalculateClassicExample() throws Exception {
        int[][] dungeon = {{-3, 5, -2}, {-1, -2, -4}, {2, -3, -1}};
        DungeonRequest dungeonRequest = new DungeonRequest(dungeon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(dungeonRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(5, jsonResponse.get("result").asInt());
        assertEquals("Success - Result saved to database", jsonResponse.get("message").asText());
    }

    @Test
    @DisplayName("Calculate single cell dungeon")
    void testCalculateSingleCell() throws Exception {
        int[][] dungeon = {{-5}};
        DungeonRequest dungeonRequest = new DungeonRequest(dungeon);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(dungeonRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertEquals(6, jsonResponse.get("result").asInt());
        assertEquals("Success - Result saved to database", jsonResponse.get("message").asText());
    }

    @Test
    @DisplayName("Get all results after saving multiple calculations")
    void testGetAllResultsIntegration() throws Exception {
        int[][] dungeon1 = {{-3, 5}};
        DungeonRequest request1 = new DungeonRequest(dungeon1);
        HttpRequest saveRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request1)))
                .build();
        httpClient.send(saveRequest1, HttpResponse.BodyHandlers.ofString());

        int[][] dungeon2 = {{10}};
        DungeonRequest request2 = new DungeonRequest(dungeon2);
        HttpRequest saveRequest2 = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(request2)))
                .build();
        httpClient.send(saveRequest2, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/results"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertTrue(jsonResponse.isArray());
        assertTrue(jsonResponse.size() >= 2);
        assertNotNull(jsonResponse.get(0).get("result"));
        assertNotNull(jsonResponse.get(1).get("result"));
    }

    @Test
    @DisplayName("Get results by dimensions")
    void testGetResultsByDimensions() throws Exception {
        int[][] dungeon = {{-3, 5}, {1, -4}};
        DungeonRequest dungeonRequest = new DungeonRequest(dungeon);
        HttpRequest saveRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(dungeonRequest)))
                .build();
        httpClient.send(saveRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/results/dimensions/2/2"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        JsonNode jsonResponse = objectMapper.readTree(response.body());
        assertTrue(jsonResponse.isArray());
        assertFalse(jsonResponse.isEmpty());
        assertEquals(2, jsonResponse.get(0).get("rows").asInt());
        assertEquals(2, jsonResponse.get(0).get("columns").asInt());
    }

    @Test
    @DisplayName("Get total count after saving calculations")
    void testGetTotalCount() throws Exception {
        int[][] dungeon = {{-1}};
        DungeonRequest dungeonRequest = new DungeonRequest(dungeon);
        HttpRequest saveRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(dungeonRequest)))
                .build();
        httpClient.send(saveRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/stats/count"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(Integer.parseInt(response.body()) >= 1);
    }

    @Test
    @DisplayName("Should handle invalid request with empty body")
    void testInvalidRequestEmptyBody() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void testMalformedJSON() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dungeon/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{invalid json}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
    }
}


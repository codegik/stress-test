package com.codegik.stress;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class DungeonGameStressTest extends Simulation {

    // Distributed test configuration - read from environment variables
    private static final int NODE_ID = getEnvAsInt("NODE_ID", 1);
    private static final int TOTAL_NODES = getEnvAsInt("TOTAL_NODES", 1);
    private static final int TOTAL_USERS = getEnvAsInt("TOTAL_USERS", 100);
    private static final int TEST_DURATION = getEnvAsInt("TEST_DURATION", 180);

    // Distribute load across nodes
    private static final int USERS_PER_NODE = TOTAL_USERS / TOTAL_NODES;

    private static final String BASE_URL = System.getenv().getOrDefault("BASE_URL", "http://localhost:8080");

    private static int getEnvAsInt(String envName, int defaultValue) {
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            try {
                return Integer.parseInt(envValue.trim());
            } catch (NumberFormatException e) {
                System.out.println("Warning: Invalid value for " + envName + ": " + envValue + ", using default: " + defaultValue);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    // HTTP protocol configuration with node identification
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/DungeonGame/Node-" + NODE_ID);

    // Test data
    private final String simpleDungeon = """
        {
            "dungeon": [
                [-3, 5],
                [1, -4]
            ]
        }
        """;

    private final String mediumDungeon = """
        {
            "dungeon": [
                [-2, -3, 4],
                [-1, -2, -2], 
                [4, 2, 1]
            ]
        }
        """;

    // Distributed mixed workload scenario - combines all operations
    private final ScenarioBuilder distributedWorkload = scenario("Distributed Workload - Node " + NODE_ID)
            .exec(
                http("health-check")
                        .get("/api/dungeon/health")
                        .check(status().is(200))
                        .check(bodyString().is("Dungeon Game API is running"))
            )
            .pause(1, 2)
            .exec(
                http("calculate-simple")
                        .post("/api/dungeon/calculate")
                        .body(StringBody(simpleDungeon))
                        .check(status().is(200))
                        .check(jsonPath("$.message").is("Success"))
                        .check(jsonPath("$.result").exists())
            )
            .pause(1, 3)
            .exec(
                http("calculate-medium")
                        .post("/api/dungeon/calculate")
                        .body(StringBody(mediumDungeon))
                        .check(status().is(200))
                        .check(jsonPath("$.message").is("Success"))
                        .check(jsonPath("$.result").exists())
            )
            .pause(1, 2)
            .exec(
                http("get-results")
                        .get("/api/dungeon/results")
                        .check(status().is(200))
                        .check(jsonPath("$").exists())
            )
            .pause(1)
            .exec(
                http("get-stats")
                        .get("/api/dungeon/stats/count")
                        .check(status().is(200))
            );

    // Distributed load test setup
    {
        System.out.println("=== Distributed Test Configuration ===");
        System.out.println("Node ID: " + NODE_ID);
        System.out.println("Total Nodes: " + TOTAL_NODES);
        System.out.println("Total Users: " + TOTAL_USERS);
        System.out.println("Users per Node: " + USERS_PER_NODE);
        System.out.println("Test Duration: " + TEST_DURATION + "s");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("=======================================");

        setUp(
            // Single scenario with distributed load
            distributedWorkload.injectOpen(
                rampUsers(USERS_PER_NODE).during(30)
            )
        )
        .protocols(httpProtocol)
        .maxDuration(TEST_DURATION + 60) // Add buffer time
        .assertions(
            global().responseTime().max().lt(5000),
            global().responseTime().mean().lt(1000),
            global().successfulRequests().percent().gt(95.0)
        );
    }
}

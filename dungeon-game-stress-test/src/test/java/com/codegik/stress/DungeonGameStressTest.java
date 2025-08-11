package com.codegik.stress;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DungeonGameStressTest extends Simulation {

    private static final String BASE_URL = "http://localhost:8080";

    // HTTP protocol configuration
    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/DungeonGame");

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

    // Health check scenario
    private final ScenarioBuilder healthCheck = scenario("Health Check")
            .exec(
                http("health-check")
                        .get("/api/dungeon/health")
                        .check(status().is(200))
                        .check(bodyString().is("Dungeon Game API is running"))
            );

    // Calculate dungeon scenario
    private final ScenarioBuilder calculateDungeon = scenario("Calculate Dungeon")
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
            );

    // Get results scenario
    private final ScenarioBuilder getResults = scenario("Get Results")
            .exec(
                http("get-all-results")
                        .get("/api/dungeon/results")
                        .check(status().is(200))
                        .check(jsonPath("$").exists())
            )
            .pause(1, 2)
            .exec(
                http("get-stats-count")
                        .get("/api/dungeon/stats/count")
                        .check(status().is(200))
            );

    // Mixed workload scenario
    private final ScenarioBuilder mixedWorkload = scenario("Mixed Workload")
            .exec(
                http("health")
                        .get("/api/dungeon/health")
                        .check(status().is(200))
            )
            .pause(1)
            .exec(
                http("calculate")
                        .post("/api/dungeon/calculate")
                        .body(StringBody(simpleDungeon))
                        .check(status().is(200))
            )
            .pause(1)
            .exec(
                http("get-results")
                        .get("/api/dungeon/results")
                        .check(status().is(200))
            );

    // Load test setup
    {
        setUp(
            healthCheck.injectOpen(
                constantUsersPerSec(2).during(30)
            ),
            calculateDungeon.injectOpen(
                rampUsersPerSec(1).to(5).during(60),
                constantUsersPerSec(5).during(120)
            ),
            getResults.injectOpen(
                rampUsersPerSec(2).to(8).during(60),
                constantUsersPerSec(8).during(120)
            ),
            mixedWorkload.injectOpen(
                rampUsersPerSec(1).to(3).during(60),
                constantUsersPerSec(3).during(120)
            )
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().max().lt(5000),
            global().responseTime().mean().lt(1000),
            global().successfulRequests().percent().gt(95.0)
        );
    }
}

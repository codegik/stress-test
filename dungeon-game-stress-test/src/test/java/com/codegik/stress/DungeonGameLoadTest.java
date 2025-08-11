package com.codegik.stress;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DungeonGameLoadTest extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private final String testDungeon = """
        {
            "dungeon": [
                [-3, 5],
                [1, -4]
            ]
        }
        """;

    private final ScenarioBuilder basicLoad = scenario("Basic Load Test")
            .exec(
                http("calculate-dungeon")
                        .post("/api/dungeon/calculate")
                        .body(StringBody(testDungeon))
                        .check(status().is(200))
            );

    {
        setUp(
            basicLoad.injectOpen(
                rampUsersPerSec(1).to(10).during(30),
                constantUsersPerSec(10).during(60)
            )
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().max().lt(3000),
            global().successfulRequests().percent().gt(99.0)
        );
    }
}

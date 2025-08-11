FROM openjdk:23-jdk-slim

WORKDIR /app

COPY target/dungeon-game-stress-test-0.0.1-SNAPSHOT.jar .
RUN chmod +x dungeon-game-stress-test-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "dungeon-game-stress-test-0.0.1-SNAPSHOT.jar"]

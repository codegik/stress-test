FROM openjdk:23-jdk-slim

ARG SIMULATION_CLASS=com.codegik.stress.DungeonGameStressTest
ARG RESULTS_FOLDER=/opt/gatling/results

ENV SIMULATION_CLASS=${SIMULATION_CLASS}
ENV RESULTS_FOLDER=${RESULTS_FOLDER}

WORKDIR /app

COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src
COPY target target

RUN mkdir -p ${RESULTS_FOLDER}

CMD ["sh", "-c", "./mvnw gatling:test -Dgatling.simulationClass=${SIMULATION_CLASS} -Dgatling.results.folder=${RESULTS_FOLDER}"]

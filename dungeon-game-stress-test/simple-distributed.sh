#!/bin/bash

# Simple Distributed Gatling Test Runner
# Runs multiple Gatling instances to simulate distributed load testing

set -e

# Default configuration
TOTAL_USERS=300
TEST_DURATION=120
NODES=3
BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}=== Simple Distributed Gatling Test ===${NC}"
echo -e "${YELLOW}Total Users: ${TOTAL_USERS}${NC}"
echo -e "${YELLOW}Test Duration: ${TEST_DURATION} seconds${NC}"
echo -e "${YELLOW}Nodes: ${NODES}${NC}"
echo -e "${YELLOW}Users per Node: $((TOTAL_USERS / NODES))${NC}"
echo -e "${YELLOW}Base URL: ${BASE_URL}${NC}"
echo ""

# Check if application is running
echo -e "${YELLOW}Checking application health...${NC}"
if ! curl -f -s "$BASE_URL/api/dungeon/health" > /dev/null 2>&1; then
    echo -e "${RED}ERROR: Application not reachable at $BASE_URL${NC}"
    echo "Make sure your application is running with: docker-compose up -d"
    exit 1
fi
echo -e "${GREEN}✓ Application is healthy${NC}"

# Create logs directory
mkdir -p logs

# Array to store background process PIDs
PIDS=()

echo -e "${BLUE}Starting distributed test nodes...${NC}"

# Start each node
for ((i=1; i<=NODES; i++)); do
    echo -e "${YELLOW}Starting Node $i...${NC}"

    ./mvnw gatling:test \
        -Dgatling.simulationClass=com.codegik.stress.DungeonGameStressTest \
        -Dbase.url="$BASE_URL" \
        -Dtotal.users="$TOTAL_USERS" \
        -Dtest.duration="$TEST_DURATION" \
        -Dnode.id="$i" \
        -Dtotal.nodes="$NODES" \
        > "logs/node_${i}.log" 2>&1 &

    PIDS+=($!)
    echo -e "${GREEN}✓ Node $i started (PID: ${PIDS[$((i-1))]})${NC}"

    # Small delay between starts
    sleep 2
done

echo ""
echo -e "${BLUE}All nodes started! Waiting for completion...${NC}"

# Wait for all nodes to complete
SUCCESS_COUNT=0
for ((i=1; i<=NODES; i++)); do
    PID=${PIDS[$((i-1))]}
    echo -e "${YELLOW}Waiting for Node $i (PID: $PID)...${NC}"

    if wait $PID; then
        echo -e "${GREEN}✓ Node $i completed successfully${NC}"
        SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
    else
        echo -e "${RED}✗ Node $i failed${NC}"
    fi
done

# Summary
echo ""
echo -e "${BLUE}=== Test Results Summary ===${NC}"
echo -e "${YELLOW}Successful Nodes: ${SUCCESS_COUNT}/${NODES}${NC}"

if [[ $SUCCESS_COUNT -eq $NODES ]]; then
    echo -e "${GREEN}🎉 All nodes completed successfully!${NC}"
else
    echo -e "${RED}⚠️  Some nodes failed. Check logs in logs/${NC}"
fi

echo ""
echo -e "${YELLOW}Individual node logs: logs/node_*.log${NC}"
echo -e "${YELLOW}Gatling reports: target/gatling/*/index.html${NC}"

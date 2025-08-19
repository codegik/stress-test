#!/bin/bash

# Colors for output (matching run.sh standard)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output (matching run.sh standard)
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_status "Waiting for all Gatling nodes to complete..."

# Function to wait for a specific node to complete
wait_for_node() {
    local node_name=$1
    print_status "Waiting for $node_name to complete..."

    while true; do
        # Container is still running, check logs for completion
        if docker compose logs $node_name | grep -q "BUILD SUCCESS"; then
            print_success "$node_name completed successfully"
            break
        elif docker compose logs $node_name | grep -q "BUILD FAILURE"; then
            print_error "$node_name failed"
            return 1
        else
            print_status "$node_name still running..."
            sleep 10
        fi

        # Check if container is still running
        if ! docker compose ps --services --filter "status=running" | grep -q "$node_name"; then
            print_status "Container $node_name is no longer running"
            return 1
        fi
    done
}

# Wait for each node to complete
wait_for_node "gatling-node-1"
wait_for_node "gatling-node-2"
wait_for_node "gatling-node-3"

print_status "All nodes completed! Consolidating results..."

# Check if simulation logs exist before merging
if ls ./logs/node*/*/simulation.log 1> /dev/null 2>&1; then
    print_status "Found simulation logs, merging..."
    mkdir -p ./logs/consolidated
    cat ./logs/node*/*/simulation.log > ./logs/consolidated/simulation.log
    print_success "Simulation logs merged successfully"

    # Generate consolidated report
    print_status "Generating consolidated report..."
    docker run --rm \
      -v $(pwd)/logs/consolidated:/opt/gatling/results \
      denvazh/gatling:latest \
      -ro /opt/gatling/results
    print_success "Consolidated report generated"
else
    print_error "No simulation logs found in ./logs/node*/simulation.log"
    print_error "Check if the Gatling tests completed successfully and generated logs"
fi

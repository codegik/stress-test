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
    mkdir -p ./target/gatling/consolidated

    cat ./logs/node*/*/simulation.log > ./target/gatling/consolidated/simulation.log
    print_success "Simulation logs merged into single file: ./target/gatling/consolidated/simulation.log"

    print_status "Generating consolidated report using Gatling Maven plugin..."
    ./mvnw gatling:test -Dgatling.reportsOnly=consolidated

    if [ $? -eq 0 ]; then
        # Find the generated report in the correct location
        REPORT_FILE=$(find "./target/gatling" -name "index.html" -path "*/consolidated/*" | head -1)
        if [[ -n "$REPORT_FILE" ]]; then
            print_success "Consolidated Gatling report generated"
            print_status "Report available at: $REPORT_FILE"
            print_status "Open in browser: file://$(pwd)/$REPORT_FILE"
        else
            print_warning "Report may have been generated but index.html not found"
            print_status "Check ./logs/consolidated/ for generated reports"
        fi
    else
        print_error "Failed to generate consolidated report using Gatling Maven plugin"
    fi
else
    print_error "No simulation logs found in ./logs/node*/simulation.log"
    print_error "Check if the Gatling tests completed successfully and generated logs"
fi

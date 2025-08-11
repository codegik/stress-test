#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${CYAN}[GATLING]${NC} $1"
}

# Function to check if application is running
check_application() {
    print_status "Checking if Dungeon Game application is running..."

    local max_attempts=10
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:8080/api/dungeon/health > /dev/null 2>&1; then
            print_success "Application is running and ready for testing"
            return 0
        fi

        print_status "Attempt $attempt/$max_attempts - Application not ready yet, waiting..."
        sleep 3
        ((attempt++))
    done

    print_error "Application is not responding at http://localhost:8080"
    print_error "You can start it with: ./run.sh"
    return 1
}

# Main execution
print_header "Dungeon Game Gatling Stress Test Runner"
print_header "========================================"
echo ""

# Check if application is running
check_application || exit 1

# Run all Gatling stress tests
print_header "Running all Gatling stress tests"
echo ""

if ./mvnw gatling:test; then
    print_success "All stress tests completed successfully!"
else
    print_error "Stress tests failed!"
    exit 1
fi

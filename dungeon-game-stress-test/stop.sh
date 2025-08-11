#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Function to detect container runtime
detect_container_runtime() {
    if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
        echo "docker compose"
    elif command -v podman >/dev/null 2>&1; then
        echo "podman compose"
    else
        echo "docker compose"  # fallback
    fi
}

# Function to stop container services
stop_containers() {
    print_status "Stopping container services..."

    local compose_command=$(detect_container_runtime)

    if $compose_command down; then
        print_success "Container services stopped successfully"
    else
        print_warning "Failed to stop containers or containers were not running"
    fi
}

# Function to stop Spring Boot application
stop_spring_app() {
    print_status "Looking for running Spring Boot application..."

    # Find Spring Boot processes
    local spring_pids=$(pgrep -f "spring-boot:run\|DungeonGameApplication\|App\.class")

    if [ -n "$spring_pids" ]; then
        print_status "Found Spring Boot application running (PIDs: $spring_pids)"
        print_status "Stopping Spring Boot application..."

        # Try graceful shutdown first
        kill $spring_pids 2>/dev/null
        sleep 3

        # Check if still running and force kill if necessary
        local remaining_pids=$(pgrep -f "spring-boot:run\|DungeonGameApplication\|App\.class")
        if [ -n "$remaining_pids" ]; then
            print_warning "Forcefully killing remaining processes..."
            kill -9 $remaining_pids 2>/dev/null
        fi

        print_success "Spring Boot application stopped"
    else
        print_status "No running Spring Boot application found"
    fi
}

# Function to stop Maven processes
stop_maven() {
    print_status "Looking for running Maven processes..."

    local maven_pids=$(pgrep -f "mvnw\|maven")

    if [ -n "$maven_pids" ]; then
        print_status "Found Maven processes (PIDs: $maven_pids)"
        print_status "Stopping Maven processes..."
        kill $maven_pids 2>/dev/null
        sleep 2
        print_success "Maven processes stopped"
    else
        print_status "No running Maven processes found"
    fi
}

# Main execution
print_status "Stopping Dungeon Game Application and Services"
print_status "=============================================="

# Stop Spring Boot application
stop_spring_app

# Stop Maven processes
stop_maven

# Stop container services
stop_containers

print_success "All services stopped successfully"
print_status "You can now restart with: ./run.sh"

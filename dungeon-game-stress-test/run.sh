#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Global variables for container runtime
CONTAINER_RUNTIME=""
COMPOSE_COMMAND=""

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

# Function to detect and set container runtime
detect_container_runtime() {
    print_status "Detecting container runtime..."

    # Check for Docker first
    if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
        CONTAINER_RUNTIME="docker"
        COMPOSE_COMMAND="docker compose"
        print_success "Docker detected and running"
        return 0
    elif command -v docker >/dev/null 2>&1; then
        print_warning "Docker is installed but not running"
    fi

    # Check for Podman
    if command -v podman >/dev/null 2>&1; then
        CONTAINER_RUNTIME="podman"
        COMPOSE_COMMAND="podman compose"
        print_success "Podman detected"
        return 0
    fi

    print_error "Neither Docker nor Podman is available or running."
    print_error "Please install and start Docker or Podman."
    exit 1
}

# Function to check container runtime
check_container_runtime() {
    if [ -z "$CONTAINER_RUNTIME" ]; then
        detect_container_runtime
    fi

    case "$CONTAINER_RUNTIME" in
        "docker")
            if ! docker info > /dev/null 2>&1; then
                print_error "Docker is not running. Please start Docker first."
                exit 1
            fi
            print_success "Docker is running"
            ;;
        "podman")
            if ! podman info > /dev/null 2>&1; then
                print_error "Podman is not running properly."
                print_status "Trying to start Podman socket..."
                if command -v systemctl >/dev/null 2>&1; then
                    systemctl --user start podman.socket || true
                fi
            fi
            print_success "Podman is ready"
            ;;
    esac
}

# Function to wait for PostgreSQL to be ready
wait_for_postgres() {
    print_status "Waiting for PostgreSQL to be ready..."
    local max_attempts=30
    local attempt=1

    while [ $attempt -le $max_attempts ]; do
        if $COMPOSE_COMMAND exec -T postgres pg_isready -U postgres -d dungeon_game > /dev/null 2>&1; then
            print_success "PostgreSQL is ready!"
            return 0
        fi

        print_status "Attempt $attempt/$max_attempts - PostgreSQL not ready yet, waiting..."
        sleep 2
        ((attempt++))
    done

    print_error "PostgreSQL failed to start within expected time"
    return 1
}

# Function to start container services
start_container_services() {
    print_status "Starting container services using $CONTAINER_RUNTIME..."

    if $COMPOSE_COMMAND up -d; then
        print_success "$CONTAINER_RUNTIME services started successfully"
    else
        print_error "Failed to start $CONTAINER_RUNTIME services"
        exit 1
    fi
}

# Function to build the application
build_application() {
    print_status "Building the application..."

    if ./mvnw clean compile; then
        print_success "Application built successfully"
    else
        print_error "Failed to build application"
        exit 1
    fi
}

# Function to run tests
run_tests() {
    print_status "Running tests..."

    if ./mvnw test; then
        print_success "All tests passed"
    else
        print_warning "Some tests failed, but continuing to start the application"
    fi
}

# Function to start the Spring Boot application
start_application() {
    print_status "Starting the Dungeon Game application..."
    print_status "Container runtime: $CONTAINER_RUNTIME"
    print_status "Application will be available at: http://localhost:8080"
    print_status "Health check: http://localhost:8080/api/dungeon/health"
    print_status ""
    print_status "Press Ctrl+C to stop the application"
    print_status ""

    # Run the application
    ./mvnw spring-boot:run
}

# Function to cleanup on exit
cleanup() {
    print_status "Shutting down..."
    print_status "Stopping container services..."

    if [ -n "$COMPOSE_COMMAND" ]; then
        $COMPOSE_COMMAND down
    else
        print_warning "Unknown container runtime, attempting docker compose cleanup..."
        docker compose down 2>/dev/null || true
    fi

    print_success "Cleanup completed"
}

# Trap Ctrl+C and call cleanup
trap cleanup EXIT

# Main execution
print_status "Starting Dungeon Game Stress Test Application"
print_status "============================================="

# Check prerequisites and detect container runtime
check_container_runtime

# Start container services
start_container_services

# Wait for PostgreSQL to be ready
wait_for_postgres

# Build application
build_application

# Run tests
run_tests

# Start the application
start_application

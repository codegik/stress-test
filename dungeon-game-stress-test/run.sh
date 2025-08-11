#!/bin/sh

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

# Function to run tests
run_tests() {
    print_status "Running tests..."

    if ./mvnw clean install; then
        print_success "All tests passed"
    else
        print_warning "Some tests failed, but continuing to start the application"
    fi
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

# Main execution
print_status "Starting Dungeon Game Stress Test Application"
print_status "============================================="

# Check prerequisites and detect container runtime
check_container_runtime
# Run tests
run_tests
# Start container services (this will start both postgres and the application)
start_container_services

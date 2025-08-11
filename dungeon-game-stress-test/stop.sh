#!/bin/sh

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

# Main execution
print_status "Stopping Dungeon Game Application and Services"
print_status "=============================================="

# Stop container services
stop_containers

print_success "All services stopped successfully"
print_status "You can now restart with: ./run.sh"

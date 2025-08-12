#!/bin/bash

# Production environment setup script
echo "Setting up BookClub production environment..."

# Generate secure passwords
DB_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 32)
ADMIN_PASSWORD=$(openssl rand -base64 16)

# Create production .env file
cat > .env << EOF
# Production Environment Variables
POSTGRES_DB=bookclub
POSTGRES_USER=bookclub_user
POSTGRES_PASSWORD=${DB_PASSWORD}

DB_HOST=postgres
DB_PORT=5432
DB_NAME=bookclub
DB_USERNAME=bookclub_user
DB_PASSWORD=${DB_PASSWORD}

JWT_SECRET=${JWT_SECRET}
ADMIN_USERNAME=admin
ADMIN_PASSWORD=${ADMIN_PASSWORD}

SPRING_PROFILES_ACTIVE=docker
SERVER_PORT=8080
LOG_LEVEL=WARN
SHOW_SQL=false
DB_POOL_SIZE=50
EOF

echo "Production .env file created with secure passwords"
echo "Admin password: ${ADMIN_PASSWORD}"
echo "Save this password securely!"
echo ""
echo "To start the application:"
echo "docker-compose up -d --build"

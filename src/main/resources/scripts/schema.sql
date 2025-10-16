-- Database schema for BoatHub application
-- This script creates the boats table based on BoatEntity

CREATE TABLE IF NOT EXISTS boats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    boat_type VARCHAR(50) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP
);

-- Check indexes in the future to see how the application evolves to be able to scale



-- Create users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create index on username for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
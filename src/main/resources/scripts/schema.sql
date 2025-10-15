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

-- Create index on boat_type for better query performance
CREATE INDEX IF NOT EXISTS idx_boats_boat_type ON boats(boat_type);

-- Create index on created_date for better query performance
CREATE INDEX IF NOT EXISTS idx_boats_created_date ON boats(created_date);

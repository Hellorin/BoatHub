-- Sample data for BoatHub application
-- This script populates the boats table with initial data

INSERT INTO boats (name, description, boat_type, created_date, updated_date) VALUES
('The Black Pearl', 'A legendary pirate ship known for its speed and mystique', 'SAILBOAT', CURRENT_TIMESTAMP - INTERVAL '30' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY),
('Titanic', 'The famous ocean liner that met its fate in the North Atlantic', 'OTHER', CURRENT_TIMESTAMP - INTERVAL '20' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY),
('Sea Breeze', 'A luxurious yacht perfect for weekend getaways', 'YACHT', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY),
('Fishing Master', 'A sturdy fishing boat designed for deep sea fishing', 'FISHING_BOAT', CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP - INTERVAL '2' HOUR);

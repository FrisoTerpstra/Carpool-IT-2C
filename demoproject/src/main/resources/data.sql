INSERT INTO locations (id, name, address) VALUES
    (1, 'Emmen', 'Van Schaikweg 94, 7811 KL Emmen'),
    (2, 'Leeuwarden', 'Rengerslaan 8-10, 8917 DD Leeuwarden'),
    (3, 'Groningen', 'Eemsgolaan 17, 9727 DW Groningen');

INSERT INTO route_distances (from_location_id, to_location_id, distance_in_km) VALUES
    (1, 2, 115.00),
    (2, 1, 115.00),
    (1, 3, 60.00),
    (3, 1, 60.00),
    (2, 3, 62.00),
    (3, 2, 62.00);

INSERT INTO emission_factors (fuel_type, co2_grams_per_km) VALUES
    ('PETROL', 202.0000),
    ('DIESEL', 176.0000),
    ('HYBRID', 145.0000),
    ('ELECTRIC', 78.0000);

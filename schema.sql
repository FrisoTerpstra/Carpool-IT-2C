-- Carpool App prototype schema for MariaDB

DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS route_distances;
DROP TABLE IF EXISTS emission_factors;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
   id BIGINT NOT NULL AUTO_INCREMENT,
   microsoft_id VARCHAR(255) NOT NULL,
   name VARCHAR(150) NOT NULL,
   email VARCHAR(255) NOT NULL,

   PRIMARY KEY (id),

   CONSTRAINT uq_users_microsoft_id
        UNIQUE (microsoft_id)
);

CREATE TABLE locations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE emission_factors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fuel_type VARCHAR(20) NOT NULL,
    co2_per_km DECIMAL(10,4) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT uq_emission_factor_fuel_type
        UNIQUE (fuel_type),

    CONSTRAINT chk_emission_factor_nonnegative
        CHECK (co2_per_km >= 0)
);

CREATE TABLE route_distances (
    id BIGINT NOT NULL AUTO_INCREMENT,
    from_location_id BIGINT NOT NULL,
    to_location_id BIGINT NOT NULL,
    distance_in_km DECIMAL(7,2) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_route_distances_from
        FOREIGN KEY (from_location_id)
        REFERENCES locations(id),

    CONSTRAINT fk_route_distances_to
        FOREIGN KEY (to_location_id)
        REFERENCES locations(id),

    CONSTRAINT uq_from_location_id_and_to_location_id
        UNIQUE (from_location_id, to_location_id),

    CONSTRAINT chk_route_distance_positive
        CHECK (distance_in_km > 0)
);

CREATE TABLE trips (
    id BIGINT NOT NULL AUTO_INCREMENT,
    driver_id BIGINT NOT NULL,
    from_location_id BIGINT NOT NULL,
    to_location_id BIGINT NOT NULL,
    emission_factor_id BIGINT NOT NULL,
    departure_time DATETIME NOT NULL,
    total_seats INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    return_group_id BIGINT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_trips_driver_id
        FOREIGN KEY (driver_id)
        REFERENCES users(id),

    CONSTRAINT fk_trips_distances_from
        FOREIGN KEY (from_location_id)
        REFERENCES locations(id),

    CONSTRAINT fk_trips_distances_to
        FOREIGN KEY (to_location_id)
        REFERENCES locations(id),

    CONSTRAINT fk_trips_emission_factor_id
        FOREIGN KEY (emission_factor_id)
        REFERENCES emission_factors(id),

    CONSTRAINT chk_trips_total_seats
        CHECK ( total_seats > 0),

    CONSTRAINT chk_trips_different_locations
        CHECK ( from_location_id <> to_location_id)
);

CREATE TABLE bookings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    passenger_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at DATETIME NOT NULL,
    responded_at DATETIME NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_bookings_passenger_id
        FOREIGN KEY (passenger_id)
        REFERENCES users(id),

    CONSTRAINT fk_bookings_trip_id
        FOREIGN KEY (trip_id)
        REFERENCES trips(id),

    CONSTRAINT uq_passenger_id_and_trip_id
        UNIQUE (passenger_id, trip_id),

    CONSTRAINT chk_bookings_status
        CHECK ( status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED'))
);

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    trip_id BIGINT NOT NULL,
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_notifications_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_notifications_trip_id
        FOREIGN KEY (trip_id)
        REFERENCES trips(id)
);
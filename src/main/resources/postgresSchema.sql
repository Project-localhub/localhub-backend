CREATE EXTENSION IF NOT EXISTS postgis;



CREATE TABLE IF NOT EXISTS store_location(

    stored_id BIGINT PRIMARY KEY,
      location GEOGRAPHY(Point, 4326)

);
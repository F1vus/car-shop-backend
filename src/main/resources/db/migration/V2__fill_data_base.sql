-- V2__fill_data_base.sql
BEGIN;

-- ===== INSERT PRODUCERS =====
INSERT INTO car_producent (name)
VALUES
    ('Toyota'),
    ('BMW'),
    ('Audi'),
    ('Mercedes-Benz'),
    ('Volkswagen'),
    ('Ford');

-- ===== INSERT COLORS =====
INSERT INTO colors (name)
VALUES
    ('Black'),
    ('White'),
    ('Red'),
    ('Blue'),
    ('Silver'),
    ('Gray');

-- ===== INSERT PETROL TYPES =====
INSERT INTO petrols (name)
VALUES
    ('Petrol'),
    ('Diesel'),
    ('Hybrid'),
    ('Electric');

COMMIT;

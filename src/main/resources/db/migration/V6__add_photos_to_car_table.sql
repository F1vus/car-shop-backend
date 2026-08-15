BEGIN;

DELETE FROM public.car_producent;
ALTER TABLE public.cars DROP COLUMN IF EXISTS image_url;

CREATE TABLE IF NOT EXISTS photos (
                                          id BIGSERIAL PRIMARY KEY,
                                          car_id BIGINT NOT NULL,
                                          photo_url TEXT NOT NULL,
                                          FOREIGN KEY (car_id) REFERENCES public.cars(id) ON DELETE CASCADE
    );


INSERT INTO car_producent (id, name)
VALUES
    (1,'Volkswagen'),
    (2,'Toyota'),
    (3,'Audi'),
    (4,'BMW'),
    (5,'Mercedes-Benz'),
    (6,'Renault'),
    (7,'Škoda'),
    (8,'Peugeot'),
    (9,'Kia'),
    (10,'Dacia'),
    (11,'Hyundai'),
    (12,'Ford'),
    (13,'Nissan'),
    (14,'Opel'),
    (15,'Tesla'),
    (16,'Porsche'),
    (17,'Citroën');

COMMIT;

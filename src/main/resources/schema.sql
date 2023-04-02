DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS friendship CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS MPA_ratings CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS likes CASCADE;

CREATE TABLE IF NOT EXISTS users (
    user_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    login     VARCHAR,
    user_name VARCHAR,
    email     VARCHAR,
    birthday  DATE
);

CREATE TABLE IF NOT EXISTS friendship (
    friendship_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id       BIGINT REFERENCES USERS (user_id),
    friend_id     BIGINT REFERENCES USERS (user_id),
    status VARCHAR
);

CREATE TABLE IF NOT EXISTS genres (
    genres_id  INT PRIMARY KEY,
    genre VARCHAR
);

CREATE TABLE IF NOT EXISTS MPA_ratings (
    rating_id   INT PRIMARY KEY,
    rating_name VARCHAR
);

CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    film_name    VARCHAR,
    description  VARCHAR,
    release_date DATE,
    duration     INT,
    rating       INT REFERENCES MPA_ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_genre_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    film_id       BIGINT REFERENCES films (film_id),
    genres_id      INT REFERENCES genres (genres_id)
);

CREATE TABLE IF NOT EXISTS likes (
    like_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    film_id BIGINT REFERENCES films (film_id),
    user_id BIGINT REFERENCES users (user_id)
);

MERGE INTO MPA_ratings
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO genres
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');
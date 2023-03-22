package ru.yandex.practicum.filmorate.DAO;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmDAO {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, Film> films = new HashMap<>();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM films;", new BeanPropertyRowMapper<>(Film.class));
    }

    @Override
    public void createFilm(@NotNull Film film) {
        jdbcTemplate.update("INSERT INTO films VALUES (1,?,?,?,?,?,?,?)", (Object) film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getLikes(), film.getGenre(), film.getRating());
    }

    @Override
    public void updateFilm(Film film) {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?,release_date = ?, duration = ?, likes = ?, " +
                        "genre = ?, rating = ? WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getLikes(),
                film.getGenre(),
                film.getRating(),
                film.getId());
    }

    @Override
    public Film findFilmById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM films WHERE film_id = ?;", new Object[]{id}, new BeanPropertyRowMapper<>(Film.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("Film not found with film_id: " + id));
    }
}


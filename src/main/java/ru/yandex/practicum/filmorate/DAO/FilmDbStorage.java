package ru.yandex.practicum.filmorate.DAO;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

public class FilmDbStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int startID;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM Films;", new BeanPropertyRowMapper<>(Film.class));
    }

    @Override
    public void createFilm(@NotNull Film film) {
        jdbcTemplate.update("INSERT INTO Films VALUES (1,?,?,?,?,?,?,?)", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getLikes(), film.getGenre(), film.getRating());
    }

    @Override
    public void updateFilm(Film film) {
        jdbcTemplate.update("UPDATE Films SET name = ?, description = ?,release_date = ?, duration = ?, likes = ?, " +
                        "genre = ?, rating = ? WHERE id = ?",
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
        return jdbcTemplate.query("SELECT * FROM Films WHERE id = ?;", new Object[]{id}, new BeanPropertyRowMapper<>(Film.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("Film not found with id: " + id));
    }

    @Override
    public Film installingLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        film.setLikes(userId);
        return film;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        film.deleteLikes(userId);
        return film;
    }

    @Override
    public Collection<Film> getPopularFilmCount(int count) {
        List<Film> popular = new ArrayList<>(films.values());
        return getTopNFilms(popular, count);
    }

    private List<Film> getTopNFilms(List<Film> films, int n) {
        Comparator<Film> comparator = new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                return film2.getLikes().size() - film1.getLikes().size();
            }
        };
        Collections.sort(films, comparator);
        return films.subList(0, Math.min(n, films.size()));
    }
}


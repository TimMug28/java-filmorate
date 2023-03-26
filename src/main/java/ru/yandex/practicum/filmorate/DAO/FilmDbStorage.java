package ru.yandex.practicum.filmorate.DAO;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

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
//        String sql = "INSERT INTO films (name, description,release_date, duration, , rating, rating_id) " +
//                " VALUES(? , ? , ? , ? , ?, ?)";
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbcTemplate.update(
//                connection -> {
//                    PreparedStatement prSt = connection.prepareStatement(
//                            sql
//                            , new String[]{"film_id"});
//                    prSt.setString(1, film.getName());
//                    prSt.setString(2, film.getDescription());
//                    prSt.setDate(4, Date.valueOf(film.getReleaseDate()));
//                    prSt.setInt(3, film.getDuration());
//                    prSt.setInt(5, film.getGenre() == null ? 0 : film.getGenre());
//                    prSt.setInt(6, film.getRating().getId());
//                    return prSt;
//                }
//                , keyHolder);
//
//
//        film.setId(keyHolder.getKey().intValue());
//
//
//
//


//        jdbcTemplate.update("INSERT INTO films VALUES (1,?,?,?,?,?,?)", (Object) film.getName(), film.getDescription(),
//                film.getReleaseDate(), film.getDuration(), film.getGenre(), film.getRating());
    }

    @Override
    public void updateFilm(Film film) {
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?,release_date = ?, duration = ?,  " +
                        "genre = ?, rating = ? WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
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

    @Override
    public Film installingLike(Integer FilmId, Integer userId) {
        return null;
    }

    @Override
    public Film deleteLike(Integer FilmId, Integer userId) {
        return null;
    }

    @Override
    public Collection<Film> getPopularFilmCount(int count) {
        return null;
    }


    static MPARating makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MPARating(
                rs.getInt("rating_id"),
                rs.getString("rating")
        );
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("genre")
        );
    }

    static Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                new MPARating(rs.getInt("rating_id"), rs.getString("rating_name"))
        );
        film.setId(rs.getInt("film_id"));
        return film;
    }
}


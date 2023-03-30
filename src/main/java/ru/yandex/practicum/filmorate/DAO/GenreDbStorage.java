package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.*;
import java.util.*;

@Repository
@Qualifier("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query("SELECT * FROM genres", GenreDbStorage::makeGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM GENRES WHERE GENRES_ID = ?", GenreDbStorage::makeGenre, id);
        if (genres.isEmpty() || genres.get(0) == null || !genres.get(0).getId().equals(id)) {
            throw new NotFoundException(String.format("Не найден Genre с id: %s", id));
        }
        return genres.get(0);
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genres_id"),
                rs.getString("genre")
        );
    }
}


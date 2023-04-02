package ru.yandex.practicum.filmorate.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.*;
import java.util.*;

@Repository
@Qualifier("MPADbStorage")
public class MPADbStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getRatings() {
        return jdbcTemplate.query("SELECT * FROM MPA_ratings", MPADbStorage::createMpa);
    }

    @Override
    public MPA getRatingById(Integer id) {
        List<MPA> ratings = jdbcTemplate.query("SELECT * FROM MPA_ratings WHERE rating_id = ?", MPADbStorage::createMpa, id);
        if (ratings.isEmpty() || ratings.get(0) == null || !ratings.get(0).getId().equals(id)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", id));
        }
        return ratings.get(0);
    }

    static MPA createMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(
                rs.getInt("rating_id"),
                rs.getString("rating_name")
        );
    }
}



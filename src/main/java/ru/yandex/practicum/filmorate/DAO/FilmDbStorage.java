package ru.yandex.practicum.filmorate.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
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
        String sqlFilmById = "SELECT * FROM films LEFT OUTER JOIN MPA_ratings ON films.rating = MPA_ratings.rating_id";
        List<Film> films = jdbcTemplate.query(sqlFilmById, FilmDbStorage::createFilm);

        for (Film film : films) {
            String sqlFilmGenres = "SELECT * " +
                    "FROM film_genres LEFT OUTER JOIN genres " +
                    "ON genres.genres_id = film_genres.genres_id " +
                    "WHERE film_id = ?";
            List<Genre> filmGenres = jdbcTemplate.query(
                    sqlFilmGenres, FilmDbStorage::createGenre, film.getId());
            film.setGenres(filmGenres);
        }
        return films;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException, NotFoundException {
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM MPA_ratings WHERE rating_id = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::createMpa, mpaId);

        if (mpaById.isEmpty() || mpaById.get(0) == null || !mpaById.get(0).getId().equals(mpaId)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", mpaId));
        }
        MPA mpa = mpaById.get(0);

        Set<Integer> genreSet = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());


        List<Genre> filmGenres = new ArrayList<>();
        for (Integer id : genreSet) {
            String sqlGenresId = "SELECT * FROM genres WHERE genres_id = ?";
            List<Genre> genresById = jdbcTemplate.query(
                    sqlGenresId, FilmDbStorage::createGenre, id);
            if (genresById.isEmpty() || genresById.get(0) == null || !genresById.get(0).getId().equals(id)) {
                throw new NotFoundException(String.format("Не найден Genre с id: %s", id));
            }
            filmGenres.add(genresById.get(0));
        }

        film.setGenres(filmGenres);
        film.setMpa(mpa);

        String sqlInsertFilm = "INSERT INTO films (film_name, description, release_date, duration, rating) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlInsertFilm, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);

        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genres_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlInsertFilmGenres, filmId, genre.getId());
        }
        return film;
    }


    @Override
    public Film updateFilm(Film film) throws NotFoundException, ValidationException {
        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM MPA_ratings WHERE rating_id = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::createMpa, mpaId);

        if (mpaById.isEmpty() || mpaById.get(0) == null || !mpaById.get(0).getId().equals(mpaId)) {
            throw new NotFoundException(String.format("Не найден MPA с id: %s", mpaId));
        }
        MPA mpa = mpaById.get(0);

        Set<Integer> genreSet = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        List<Genre> filmGenres = new ArrayList<>();
        for (Integer id : genreSet) {
            String sqlGenresId = "SELECT * FROM genres WHERE genres_id = ?";
            List<Genre> genresById = jdbcTemplate.query(
                    sqlGenresId, FilmDbStorage::createGenre, id);
            if (genresById.isEmpty() || genresById.get(0) == null || !genresById.get(0).getId().equals(id)) {
                throw new NotFoundException(String.format("Не найден Genre с id: %s", id));
            }
            filmGenres.add(genresById.get(0));
        }
        film.setGenres(filmGenres);
        film.setMpa(mpa);
        if (getFilmById(film.getId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        String sqlUpdateFilm = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, rating = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        String sqlDeleteFilmGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilmGenres, film.getId());

        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genres_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlInsertFilmGenres, film.getId(), genre.getId());
        }
        return film;
    }


    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT * FROM films " +
                "LEFT OUTER JOIN mpa_ratings " +
                "ON films.rating = mpa_ratings.rating_id " +
                "WHERE film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, FilmDbStorage::createFilm, id);
        if (films.isEmpty()) {
            return null;
        }
        Film film = films.get(0);

        String sqlGenres = "SELECT * FROM film_genres " +
                "LEFT OUTER JOIN genres " +
                "ON film_genres.genres_id = genres.genres_id " +
                "WHERE film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlGenres, FilmDbStorage::createGenre, id);
        film.setGenres(genres);
        return film;
    }

    @Override
    public Film installingLike(Integer filmId, Integer userId) {
        String sqlCreateLike = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlCreateLike, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sqlFilmLikesById = "SELECT * FROM likes WHERE film_id = ?";
        List<Integer> likesList = jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt(
                "user_id"), filmId);
        if (!likesList.contains(userId)) {
            throw new NotFoundException(String.format("Не найден фильм с like от пользователя: %s", userId));
        }
        String sqlDeleteLike = "DELETE FROM likes " +
                "WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlDeleteLike, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilmCount(int count) {
        Collection<Film> popular = getFilms();
        if (popular.size() <= 1) {
            return popular;
        } else {
            return popular.stream()
                    .sorted((film1, film2) ->
                            Integer.compare(
                                    getFilmLikes(film2.getId()).size(),
                                    getFilmLikes(film1.getId()).size())
                    )
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }

    public List<Integer> getFilmLikes(Integer id) {
        String sqlFilmLikesById = "SELECT * FROM likes WHERE film_id = ?";
        return jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("user_id"), id);
    }

    static MPA createMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(
                rs.getInt("rating_id"),
                rs.getString("rating_name")
        );
    }

    static Genre createGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genres_id"),
                rs.getString("genre")
        );
    }

    public static Film createFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("film_name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        MPA rating = new MPA();
        rating.setId(rs.getInt("rating_id"));
        rating.setName(rs.getString("rating_name"));
        film.setMpa(rating);
        return film;
    }
}


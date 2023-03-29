package ru.yandex.practicum.filmorate.DAO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
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


        String sqlFilmById = "SELECT * FROM FILMS LEFT OUTER JOIN MPA_ratings ON FILMS.RATING = MPA_ratings.RATING_ID";
        List<Film> films = jdbcTemplate.query(sqlFilmById, FilmDbStorage::makeFilm);

        for (Film film : films) {
            //Создание списка жанров для конкретного film
            String sqlFilmGenres = "SELECT * " +
                    "FROM film_genres LEFT OUTER JOIN genres " +
                    "ON genres.genres_id = film_genres.genres_id " +
                    "WHERE film_id = ?";
            List<Genre> filmGenres = jdbcTemplate.query(
                    sqlFilmGenres, FilmDbStorage::makeGenre, film.getId());
            //Установка жанров для film
            film.setGenres(filmGenres);
        }

        //Вовзрат списка готовых film с жанрами
        return films;
    }

    @Override
    public Film createFilm(Film film) throws ValidationException, NotFoundException {

        Integer mpaId = film.getMpa().getId();
        String sqlMpa = "SELECT * FROM MPA_ratings WHERE rating_id = ?";
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::makeMpa, mpaId);
        validateExistMPA(mpaById, mpaId);
        MPA mpa = mpaById.get(0);

        Set<Integer> genreSet = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());


        List<Genre> filmGenres = new ArrayList<>();
        for (Integer id : genreSet) {
            String sqlGenresId = "SELECT * FROM genres WHERE genres_id = ?";
            List<Genre> genresById = jdbcTemplate.query(
                    sqlGenresId, FilmDbStorage::makeGenre, id);
            validateExistGenre(genresById, id);
            filmGenres.add(genresById.get(0));
        }

        film.setGenres(filmGenres);
        film.setMpa(mpa);

        // Добавление записи в таблицу films
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

        // Получение id добавленного фильма
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);

        // Добавление записей в таблицу film_genres для каждого жанра фильма
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
        List<MPA> mpaById = jdbcTemplate.query(sqlMpa, FilmDbStorage::makeMpa, mpaId);

validateExistMPA(mpaById, mpaId);

        MPA mpa = mpaById.get(0);

        Set<Integer> genreSet = film.getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());


        List<Genre> filmGenres = new ArrayList<>();
        for (Integer id : genreSet) {
            String sqlGenresId = "SELECT * FROM genres WHERE genres_id = ?";
            List<Genre> genresById = jdbcTemplate.query(
                    sqlGenresId, FilmDbStorage::makeGenre, id);

validateExistGenre(genresById, id);

            filmGenres.add(genresById.get(0));
        }

        film.setGenres(filmGenres);
        film.setMpa(mpa);
               // Проверка существования фильма в базе данных
        if (getFilmById(film.getId()) == null) {
            throw new NotFoundException("Фильм не найден");
        }

        // Обновление записи в таблице films
        String sqlUpdateFilm = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, rating = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        // Удаление всех записей о жанрах фильма из таблицы film_genres
        String sqlDeleteFilmGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilmGenres, film.getId());

        // Добавление записей о жанрах фильма в таблицу film_genres
        String sqlInsertFilmGenres = "INSERT INTO film_genres (film_id, genres_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlInsertFilmGenres, film.getId(), genre.getId());
        }
        return film;
    }



    @Override
    public Film getFilmById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM films WHERE film_id = ?;", new Object[]{id}, new BeanPropertyRowMapper<>(Film.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Film not found with film_id: " + id));
    }

    @Override
    public Film installingLike(Integer filmId, Integer userId) {
        //Валидация user_id
        String sqlFilmLikesById = "SELECT * FROM likes WHERE FILM_ID = ?";
        List<Integer> filmLikesList = jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("USER_ID"), filmId);
       // validateLike(filmLikesList, userId);

        //Запись like в DB
        String sqlCreateLike =
                "INSERT INTO likes(" +
                        "FILM_ID, " +
                        "USER_ID) " +
                        "VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlCreateLike, new String[]{"LIKE_ID"});
            stmt.setLong(1, filmId);
            stmt.setLong(2, userId);
            return stmt;
        }, keyHolder);
        Integer likeId = keyHolder.getKey().intValue();
        return getFilmById(filmId);
    }


    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        //Валидация user_id
        String sqlFilmLikesById = "SELECT * FROM likes WHERE FILM_ID = ?";
        List<Long> filmLikesList = jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getLong("USER_ID"), filmId);
        //validateExist(filmLikesList, userId);

        //Удаление записи like в DB
        String sqlDeleteLike = "DELETE FROM likes " +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlDeleteLike, filmId, userId);
    }

    @Override
    public Collection<Film> getPopularFilmCount(int count) {
        Collection<Film> popular = getFilms();
        if (popular.size() <= 1) {
            return popular;
        } else {
            return popular.stream()
                    .sorted((film1, film2) -> {
                        int result =
                                Integer.compare(
                                        getFilmLikes(film1.getId()).size()
                                        , getFilmLikes(film2.getId()).size());
                        result = -1 * result;
                        return result;
                    })
                    .limit(count)
                    .collect(Collectors.toList());
        }
    }
    public List<Integer> getFilmLikes(Integer id) {
        //Возврат likes
        String sqlFilmLikesById = "SELECT * FROM likes WHERE FILM_ID = ?";
        List<Integer> filmLikesList = jdbcTemplate.query(sqlFilmLikesById, (rs, rowNum) -> rs.getInt("USER_ID"), id);
        return filmLikesList;
    }
//    public Collection<Film> getpularFilmCount(int count) {
//        List<Film> popular = new ArrayList<>(getFilms());
//        return getTopNFilms(popular, count);
//    }
//
//    private List<Film> getTopNFilms(List<Film> films, int n) {
//        Comparator<Film> comparator = new Comparator<Film>() {
//            @Override
//            public int compare(Film film1, Film film2) {
//                return film2.getLikes().size() - film1.getLikes().size();
//            }
//        };
//        Collections.sort(films, comparator);
//        return null;//films.subList(0, Math.min(n, films.size()));
//    }


    static MPA makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new MPA(
                rs.getInt("rating_id"),
                rs.getString("rating_name")
        );
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genres_id"),
                rs.getString("genre")
        );
    }

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
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

    public static void validateMPA(List<Integer> mpaIdList, Integer id) throws ValidationException {
        if (!mpaIdList.contains(id)) {
            throw new ValidationException(String.format("MPA with %s is not found", id));
        }
    }

    public static void validateExistMPA(List<MPA> mpaIdList, Integer id) throws NotFoundException {
        if (mpaIdList.isEmpty() || mpaIdList.get(0) == null || !mpaIdList.get(0).getId().equals(id)) {
            throw new NotFoundException(String.format("MPA with %s is not found", id));
        }
    }

    public static void validateGenre(List<Integer> genreList, Integer id) throws ValidationException {
        if (!genreList.contains(id)) {
            throw new ValidationException(String.format("Genre with %s is not found", id));
        }
    }

    public static void validateExistGenre(List<Genre> genreList, Integer id) throws NotFoundException {
        if (genreList.isEmpty() || genreList.get(0) == null || !genreList.get(0).getId().equals(id)) {
            throw new NotFoundException(String.format("Genre with %s is not found", id));
        }
    }
}


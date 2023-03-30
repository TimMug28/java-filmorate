package ru.yandex.practicum.filmorate.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;


@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        validate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public Film installingLike(Integer filmId, Integer userId) {

        validateLike(filmId, userId);
        return filmStorage.installingLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public Film getFilmById(Integer id) {
        if (filmStorage.getFilmById(id) == null) {
            log.error("Не найден фильм c id {}.", id);
            throw new NotFoundException("Фильм " + id);
        }
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getPopularFilmCount(int count) {
        return filmStorage.getPopularFilmCount(count);
    }

    private void validate(Film film) {
        if (film.getName().isBlank()) {
            log.error("Пустое имя фильма", FilmService.class);
            throw new ValidationException("Название не должно быть пустым");
        }
        if (film.getDescription().isBlank() || film.getDescription().isEmpty() || film.getDescription().length() > 200) {
            log.error("Описание фильма больше 200 символов", FilmService.class);
            throw new ValidationException("Описание не должно быть пустым или превышать 200 символов");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма отрицательная", FilmService.class);
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void validateLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            log.error("Отрицательный id");
            throw new NotFoundException("Введен отрицательный id.");
        }
        if (getFilmById(filmId) == null) {
            log.error("Не найден фильм c id {}.", filmId);
            throw new NotFoundException("Фильм не найден " + filmId);
        }
    }
}
package ru.yandex.practicum.filmorate.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;


@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
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
        if (findFilmById(filmId).getLikes().contains(userId)) {
            log.error("Фильм уже добавлен c id {}.", filmId);
            throw new ValidationException("Двойной лайк фильма " + filmId);
        }
        validateLike(filmId, userId);
        User user = userService.findUserById(userId);
        user.setLikeFilms(filmId);
        return filmStorage.installingLike(filmId, userId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        validateLike(filmId, userId);
        User user = userService.findUserById(userId);
        user.deleteLikeFilm(filmId);
        return filmStorage.deleteLike(filmId, userId);
    }

    public Film findFilmById(Integer id) {
        if (filmStorage.findFilmById(id) == null) {
            log.error("Не найден фильм c id {}.", id);
            throw new NotFoundException("Фильм " + id);
        }
        return filmStorage.findFilmById(id);
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
        if (userService.findUserById(userId) == null) {
            log.error("Не найден фильм c id {}.", userId);
            throw new NotFoundException("фильм не найден " + userId);
        }
        if (findFilmById(filmId) == null) {
            log.error("Не найден фильм c id {}.", filmId);
            throw new NotFoundException("Фильм не найден " + filmId);
        }
    }
}
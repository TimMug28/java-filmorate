package ru.yandex.practicum.filmorate.storage;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int startID;

    @Autowired
    public InMemoryFilmStorage() {
        startID = 1;
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(@NotNull Film film) {
        Integer id = startID;
        startID++;
        film.setId(id);
        films.put(film.getId(), film);
        log.debug("Данные добавлены для фильма {}.", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Введён несуществующий id", FilmService.class);
            throw new NotFoundException("Фильма с id  " + film.getId() + " не существует");
        }
        Film updateFilm = films.get(film.getId());
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        films.put(film.getId(), updateFilm);
        log.debug("Обновлены данные фильма {}.", updateFilm.getId());
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film installingLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
    }

    @Override
    public Collection<Film> getPopularFilmCount(int count) {
        return null;
    }
}

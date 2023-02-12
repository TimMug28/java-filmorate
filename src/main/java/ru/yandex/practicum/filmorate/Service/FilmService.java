package ru.yandex.practicum.filmorate.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class FilmService {
    protected final Map<Integer, Film> films = new HashMap<>();
    private int startID;

    FilmService() {
        startID = 1;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film createFilm(Film film) {
        Integer id = startID;
        startID++;
        film.setId(id);
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильма с id  " + film.getId() + " не существует");
        }
        Film updateFilm = films.get(film.getId());
        updateFilm.setName(film.getName());
        updateFilm.setDescription(film.getDescription());
        updateFilm.setReleaseDate(film.getReleaseDate());
        updateFilm.setDuration(film.getDuration());
        films.put(film.getId(), updateFilm);
        log.debug("Обновлены данные фильма {}.", updateFilm.getId());
        return updateFilm;
    }

    public Film findFilmById(Integer id) {
        return films.get(id);
    }
}
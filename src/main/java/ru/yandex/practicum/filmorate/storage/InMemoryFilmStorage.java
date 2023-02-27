package ru.yandex.practicum.filmorate.storage;

import org.apache.catalina.LifecycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage  implements FilmStorage  {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int startID;

    public InMemoryFilmStorage() {
        startID = 1;
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        Integer id = startID;
        startID++;
        film.setId(id);
        films.put(film.getId(), film);
        return film;
    }


    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Введён несуществующий id", FilmService.class);
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

    @Override
    public Film findFilmById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film installingLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        film.setLikesCounter(userId);
        return film;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = films.get(filmId);
        film.deleteLikesCounter(userId);
        return film;
    }

    @Override
    public Collection<Film> getPopularFilm(int count) {
        List<Film> popular = new ArrayList<>(films.values());
      //  popular.sort(Comparator.comparing(Film::getLikesCounterSize));
        List<Film> result = (ArrayList<Film>) popular
                .stream().sorted(Comparator.comparing(Film::getLikesCounterSize))
                .limit(count)
                .collect(Collectors.toList());
        return result;
    }

}

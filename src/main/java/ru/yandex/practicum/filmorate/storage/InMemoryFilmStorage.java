//package ru.yandex.practicum.filmorate.storage;
//
//import org.jetbrains.annotations.NotNull;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.util.*;
//
//@Component
//public class InMemoryFilmStorage implements FilmStorage {
//    private static final Logger log = LoggerFactory.getLogger(UserService.class);
//    private final Map<Integer, Film> films = new HashMap<>();
//    private int startID;
//
//    @Autowired
//    public InMemoryFilmStorage() {
//        startID = 1;
//    }
//
//    @Override
//    public Collection<Film> getFilms() {
//        return films.values();
//    }
//
//    @Override
//    public void createFilm(@NotNull Film film) {
//        Integer id = startID;
//        startID++;
//        film.setId(id);
//        films.put(film.getId(), film);
//        log.debug("Данные добавлены для фильма {}.", film.getId());
//    }
//
//    @Override
//    public void updateFilm(Film film) {
//        if (!films.containsKey(film.getId())) {
//            log.error("Введён несуществующий id", FilmService.class);
//            throw new NotFoundException("Фильма с id  " + film.getId() + " не существует");
//        }
//        Film updateFilm = films.get(film.getId());
//        updateFilm.setName(film.getName());
//        updateFilm.setDescription(film.getDescription());
//        updateFilm.setReleaseDate(film.getReleaseDate());
//        updateFilm.setDuration(film.getDuration());
//        films.put(film.getId(), updateFilm);
//        log.debug("Обновлены данные фильма {}.", updateFilm.getId());
//    }
//
//    @Override
//    public Film findFilmById(Integer id) {
//        return films.get(id);
//    }
//
//    @Override
//    public Film installingLike(Integer filmId, Integer userId) {
//        Film film = films.get(filmId);
//        film.setLikes(userId);
//        return film;
//    }
//
//    @Override
//    public Film deleteLike(Integer filmId, Integer userId) {
//        Film film = films.get(filmId);
//        film.deleteLikes(userId);
//        return film;
//    }
//
//    @Override
//    public Collection<Film> getPopularFilmCount(int count) {
//        List<Film> popular = new ArrayList<>(films.values());
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
//        return films.subList(0, Math.min(n, films.size()));
//    }
//}

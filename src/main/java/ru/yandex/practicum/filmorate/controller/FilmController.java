package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping("/films")
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@Valid @PathVariable("id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film installingLike(@Valid @PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.installingLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@Valid @PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilmCount(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilmCount(count);
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable(name = "id") Integer id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MPA> findAllRatings() {
        return filmService.getRatings();
    }

    @GetMapping("/mpa/{id}")
    public MPA findRatingById(@PathVariable(name = "id") Integer id) {
        return filmService.getRatingById(id);
    }
}

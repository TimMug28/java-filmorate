package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

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
    public void createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
    }

    @PutMapping("/films")
    public void updateFilm(@Valid @RequestBody Film film) {
        filmService.updateFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@Valid @PathVariable("id") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film installingLike(@Valid @PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.installingLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@Valid @PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilmCount(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilmCount(count);
    }
}

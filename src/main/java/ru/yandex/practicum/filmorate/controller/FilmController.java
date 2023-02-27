package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/film/{filmId}")
    public Film getFilm(@Valid @PathVariable("filmId") Integer filmId) {
        return filmService.findFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film installingLike(@Valid @PathVariable ("id") Integer filmId, @PathVariable ("userId") Integer userId) {
        return filmService.installingLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike(@Valid @PathVariable ("id") Integer filmId, @PathVariable  ("userId") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular?count={count}")
    public Collection<Film> getPopularFilm(@Valid @PathVariable("count") Integer count) {
        return filmService.getPopularFilm(count);
    }
}



package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getFilms();

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public Film getFilmById(Integer id);

    public Film installingLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);

    public Collection<Film> getPopularFilmCount(int count);
}

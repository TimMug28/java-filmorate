package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getFilms();

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public Film findFilmById(Integer id);

    public Film installingLike(Integer FilmId, Integer userId);

    public Film deleteLike(Integer FilmId, Integer userId);

    public Collection <Film> getPopularFilmCount (int count);
}

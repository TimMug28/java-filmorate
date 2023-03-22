package ru.yandex.practicum.filmorate.DAO;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmDAO {
    public Collection<Film> getFilms();

    public void createFilm(Film film);

    public void updateFilm(Film film);

    public Film findFilmById(Integer id);
}

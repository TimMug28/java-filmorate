package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    public Collection<Film> getFilms();

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public Film getFilmById(Integer id);

    public Film installingLike(Integer FilmId, Integer userId);

    public void deleteLike(Integer FilmId, Integer userId);

    public Collection <Film> getPopularFilmCount (int count);

    public List<Genre> getGenres();

    public Genre getGenreById(Integer id);

    public List<MPA> getRatings();

    public MPA getRatingById(Integer id);
}

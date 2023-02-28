package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Service.FilmService;
import ru.yandex.practicum.filmorate.Service.UserService;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController controller;
    private Film film;
    private Film film1;
    private Film film2;
    private Film filmBad;
    private Film filmBad2;
    private Film filmBad3;
    private Film filmBad5;
    private Film filmBad4;

    @BeforeEach
    void start() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new UserService(
                new InMemoryUserStorage())));
        createFilms();
    }

    private void createFilms() {
        film = new Film("Фильм", "Описание фильма до 200 символов",
                LocalDate.of(2012, 1, 1), 100);
        film1 = new Film("Фильм2", "Описание фильма2 до 200 символов",
                LocalDate.of(2002, 1, 1), 102);
        film2 = new Film("Фильм3", "Описание фильма3 до 200 символов",
                LocalDate.of(2001, 1, 1), 102);
        filmBad = new Film("Фильм3", "Описание фильма3 более 200 символов Фильмов много — и с " +
                "каждым годом становится всё больше. Чем их больше, тем больше разных оценок. Чем больше оценок, тем " +
                "сложнее сделать выбор. Однако не время сдаваться! Вы напишете бэкенд для сервиса, который будет " +
                "работать с фильмами и оценками пользователей, а также возвращать топ-5 фильмов, рекомендованных к " +
                "просмотру. Теперь ни вам, ни вашим друзьям не придётся долго размышлять, что посмотреть вечером.",
                LocalDate.of(2002, 1, 1), 102);
        filmBad2 = new Film("", "Описание фильма4 до 200 символов",
                LocalDate.of(2002, 1, 1), 102);
        filmBad3 = new Film("Фильм5", "Описание фильма5 до 200 символов",
                LocalDate.of(2002, 1, 1), -60);
        filmBad5 = new Film("Фильм5", "Описание фильма5 до 200 символов",
                LocalDate.of(2002, 1, 1), 0);
        filmBad4 = new Film("Фильм6", "Описание фильма3 = 200 символов Фильмов много — и с каждым годом" +
                " становится всё больше. Чем их больше, тем больше разных оценок. Чем больше оценок, тем сложнее сделать" +
                " выбор. Однако  время сдаваться!",
                LocalDate.of(2000, 1, 1), 90);

    }

    @Test
    void createFilm() {
        assertEquals(0, controller.getFilms().size(), "Хранилище не пустое");
        controller.createFilm(film);
        controller.createFilm(film1);
        Collection<Film> films = controller.getFilms();
        assertEquals(2, films.size(), "Хранилище пустое");
        assertTrue(films.contains(film), "Фильм не добавлен");
        assertTrue(films.contains(film1), "Фильм не добавлен");

    }

    @Test
    void createLikeFilm() {
        assertEquals(0, controller.getFilms().size(), "Хранилище не пустое");
        controller.createFilm(film);
        controller.createFilm(film1);
        controller.createFilm(film2);

        controller.installingLike(1, 1);
        Collection<Film> films = controller.getFilms();
        assertEquals(2, films.size(), "Хранилище пустое");
        assertTrue(films.contains(film), "Фильм не добавлен");
        assertTrue(films.contains(film1), "Фильм не добавлен");

    }

    @Test
    void createMovieWithoutName() {
        assertThrows(ValidationException.class, () -> controller.createFilm(filmBad2), "Название фильма");
        assertFalse(controller.getFilms().contains(filmBad2), "Фильм добавлен в хранилище.");
    }

    @Test
    void createMovieWithDescriptionOfMoreThan200Characters() {
        assertThrows(ValidationException.class, () -> controller.createFilm(filmBad), "Описание 200");
        assertFalse(controller.getFilms().contains(filmBad), "Фильм добавлен");
    }

    @Test
    void createMovieWithDescriptionEqual200Characters() {
        controller.createFilm(filmBad4);
        assertTrue(controller.getFilms().contains(filmBad4), "Фильм не добавлен");
    }

    @Test
    void createWithIncorrectReleaseDate() {
        assertThrows(ValidationException.class, () -> controller.createFilm(new Film("Фильм5", "Описание" +
                        " фильма5 до 200 символов", LocalDate.of(1770, 1, 1), 90)),
                "Дата выхода фильма позже 28.12.1895");
    }

    @Test
    void createDurationOfTheMovieIs0() {
        assertThrows(ValidationException.class, () -> controller.createFilm(filmBad5), "Продолжительность 0");
        assertFalse(controller.getFilms().contains(filmBad5), "Фильм добавлен");
    }

    @Test
    void createDurationOfTheMovieIsNegative() {
        assertThrows(ValidationException.class, () -> controller.createFilm(filmBad3),
                "Продолжительность фильма положительная.");
        assertFalse(controller.getFilms().contains(filmBad3), "Фильм добавлен в хранилище.");
    }
}
package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.DAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.DAO.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DAOFilmTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    @DirtiesContext
    void testFindFilmById() {
        Film testFilm = new Film();
        testFilm.setId(1);
        testFilm.setName("Фильм1");
        testFilm.setDescription("Всякое разное описание тестового фильма");
        testFilm.setReleaseDate(LocalDate.of(2012, 1, 1));
        testFilm.setDuration(100);
        testFilm.setMpa(new MPA(4, "R"));
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(testFilm.getId()));

        assertThat(filmOptional).isPresent();
        Film film = filmOptional.get();
        assertThat(film.getId()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testGetFilms() {
        Film testFilm1 = new Film();
        testFilm1.setId(1);
        testFilm1.setName("Фильм1");
        testFilm1.setDescription("Всякое разное описание тестового фильма");
        testFilm1.setReleaseDate(LocalDate.of(2012, 1, 1));
        testFilm1.setDuration(100);
        testFilm1.setMpa(new MPA(1, "R"));
        filmStorage.createFilm(testFilm1);

        Film testFilm2 = new Film();
        testFilm2.setId(2);
        testFilm2.setName("Фильм2");
        testFilm2.setDescription("Всякое разное описание тестового фильма");
        testFilm2.setReleaseDate(LocalDate.of(2013, 2, 2));
        testFilm2.setDuration(150);
        testFilm2.setMpa(new MPA(2, "PG-13"));
        filmStorage.createFilm(testFilm2);

        Collection<Film> films = filmStorage.getFilms();

        assertEquals(2, films.size());
    }

    @Test
    @DirtiesContext
    void testCreateFilm() {
        Film testFilm = new Film();
        testFilm.setId(1);
        testFilm.setName("Фильм1");
        testFilm.setDescription("Всякое разное описание тестового фильма");
        testFilm.setReleaseDate(LocalDate.of(2012, 1, 1));
        testFilm.setDuration(100);
        testFilm.setMpa(new MPA(1, "R"));
        filmStorage.createFilm(testFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(testFilm.getId()));

        assertThat(filmOptional).isPresent();
        Film film = filmOptional.get();
        assertThat(film.getId()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testUpdateFilm() {
        Film testFilm = new Film();
        testFilm.setId(1);
        testFilm.setName("Фильм1");
        testFilm.setDescription("Всякое разное описание тестового фильма");
        testFilm.setReleaseDate(LocalDate.of(2012, 1, 1));
        testFilm.setDuration(100);
        testFilm.setMpa(new MPA(1, "R"));
        filmStorage.createFilm(testFilm);

        Film updatedFilm = new Film();
        updatedFilm.setId(testFilm.getId());
        updatedFilm.setName("Новое название");
        updatedFilm.setDescription(testFilm.getDescription());
        updatedFilm.setReleaseDate(testFilm.getReleaseDate());
        updatedFilm.setDuration(testFilm.getDuration());
        updatedFilm.setMpa(testFilm.getMpa());
        filmStorage.updateFilm(updatedFilm);

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(testFilm.getId()));

        assertThat(filmOptional).isPresent();
        Film film = filmOptional.get();
        assertThat(film.getName()).isEqualTo("Новое название");
    }

    @Test
    @DirtiesContext
    void testInstallingLike() {
        Film testFilm = new Film();
        testFilm.setId(1);
        testFilm.setName("Фильм1");
        testFilm.setDescription("Всякое разное описание тестового фильма");
        testFilm.setReleaseDate(LocalDate.of(2012, 1, 1));
        testFilm.setDuration(100);
        testFilm.setMpa(new MPA(1, "R"));
        filmStorage.createFilm(testFilm);

        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("super@mail.ru");
        testUser.setLogin("bob");
        testUser.setName("BigBob");
        testUser.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser);

        Film likedFilm = filmStorage.installingLike(testFilm.getId(), testUser.getId());

        assertThat(likedFilm).isNotNull();
        assertThat(likedFilm.getId()).isEqualTo(testFilm.getId());
    }

    @Test
    @DirtiesContext
    void testDeleteLike() {
        Film testFilm = new Film();
        testFilm.setId(1);
        testFilm.setName("Фильм1");
        testFilm.setDescription("Всякое разное описание тестового фильма");
        testFilm.setReleaseDate(LocalDate.of(2012, 1, 1));
        testFilm.setDuration(100);
        testFilm.setMpa(new MPA(1, "R"));
        filmStorage.createFilm(testFilm);

        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("super@mail.ru");
        testUser.setLogin("bob");
        testUser.setName("BigBob");
        testUser.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser);

        Film likedFilm = filmStorage.installingLike(testFilm.getId(), testUser.getId());

        assertThat(likedFilm).isNotNull();
        assertThat(likedFilm.getId()).isEqualTo(testFilm.getId());

        filmStorage.deleteLike(testFilm.getId(), testUser.getId());

        Film unlikedFilm = filmStorage.getFilmById(testFilm.getId());

        assertThat(unlikedFilm).isNotNull();
        assertThat(unlikedFilm.getId()).isEqualTo(testFilm.getId());
    }
}
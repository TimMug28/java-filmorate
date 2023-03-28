package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class Film {
    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Количество символов в описании не должно превышать 200 символов")
    private String description;
    @Setter(AccessLevel.NONE)
    private LocalDate releaseDate;
    @PositiveOrZero
    private Integer duration;
    private List<Integer> genre = new ArrayList<>();
    private MPARating rating;

    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration,
               List <Integer> genre, MPARating rating) {
        this.id = id;
        this.name = name;
        this.description = description;
        setReleaseDate(releaseDate);
        this.duration = duration;
        this.genre = genre;
        this.rating = rating;
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration,
                List <Integer> genre,  MPARating rating) {
        this.name = name;
        this.description = description;
        setReleaseDate(releaseDate);
        this.duration = duration;
        this.genre = genre;
        this.rating = rating;
    }

    public Film(String name, String description, LocalDate releaseDate, int duration, MPARating mpaRating) {
        this.name = name;
        this.description = description;
        setReleaseDate(releaseDate);
        this.duration = duration;
        this.rating = mpaRating;
    }

    public Film(int film_id, String email, String description, LocalDate releaseDate) {
    }

    public void setReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Фильм не может быть выпущен раньше 28 декабря 1895 года");
        }
        this.releaseDate = releaseDate;
    }
}

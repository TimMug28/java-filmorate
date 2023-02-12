package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
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


    public Film(Integer id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        setReleaseDate(releaseDate);
        this.duration = duration;
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        setReleaseDate(releaseDate);
        this.duration = duration;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Фильм не может быть выпущен раньше 28 декабря 1895 года");
        }
        this.releaseDate = releaseDate;
    }
}

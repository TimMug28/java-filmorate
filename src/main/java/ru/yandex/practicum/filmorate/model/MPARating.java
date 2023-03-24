package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class MPARating {
    private Integer id;
    private String name;

    public MPARating(int id) {
        this.id = id;
    }
}
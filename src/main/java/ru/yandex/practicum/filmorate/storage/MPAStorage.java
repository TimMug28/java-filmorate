package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface MPAStorage {

    public List<MPA> getRatings();

    public MPA getRatingById(Integer id);
}

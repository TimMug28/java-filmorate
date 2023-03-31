package ru.yandex.practicum.filmorate.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;

@Service
public class MPAService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final MPAStorage mpaStorage;

    @Autowired
    public MPAService(@Qualifier("MPADbStorage") MPAStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MPA> getRatings() {
        return mpaStorage.getRatings();
    }

    public MPA getRatingById(Integer id) {
        return mpaStorage.getRatingById(id);
    }
}
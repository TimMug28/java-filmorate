package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RestController
@RequestMapping
public class MPAController {
    private final MPAService mpaService;

    @Autowired
    public MPAController(MPAService m) {
        this.mpaService = m;
    }

    @GetMapping("/mpa")
    public List<MPA> getRatings() {
        return mpaService.getRatings();
    }

    @GetMapping("/mpa/{id}")
    public MPA getRatingById(@PathVariable(name = "id") Integer id) {
        return mpaService.getRatingById(id);
    }
}
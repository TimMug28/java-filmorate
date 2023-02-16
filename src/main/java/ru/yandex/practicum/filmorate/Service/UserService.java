package ru.yandex.practicum.filmorate.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int startID;

    public UserService() {
        startID = 1;
    }

    public Collection<User> getUsersValue() {
        return users.values();
    }

    public User createUser(User user) {
        validate(user);
        Integer id = startID;
        startID++;
        user.setId(id);
        users.put(user.getId(), user);
        log.debug("Данные добавлены для пользователя {}.", user.getId());
        return user;
    }

    public User updateUser(User user) {
        validate(user);
        if (!users.containsKey(user.getId())) {
            log.error("Введен несуществующий id", UserService.class);
            throw new ValidationException("Пользователя с id  " + user.getId() + " не существует");
        }
        users.put(user.getId(), user);
        log.debug("Обновлены данные пользователя {}.", user.getId());
        return user;
    }

    public User findUserById(Integer id) {
        return users.get(id);
    }

    private void validate(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Пустой E-mail или отсутствует символ - @", UserService.class);
            throw new ValidationException("Email не должен быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Введен пустой логин, либо он содержит пробелы", UserService.class);
            throw new ValidationException("Логин пустой или содержит пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Указана дата рождения из будущего", UserService.class);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
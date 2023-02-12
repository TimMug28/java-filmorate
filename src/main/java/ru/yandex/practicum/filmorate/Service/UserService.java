package ru.yandex.practicum.filmorate.Service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserService {
    protected final Map<Integer, User> users = new HashMap<>();
    private int startID;

    public UserService() {
        startID = 1;
    }

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public Collection<User> getUsersValue() {
        return users.values();
    }

    public User createUser(User user) {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email не должен быть пустым и должен содержать символ @.");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин пустой или содержит пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        Integer id = startID;
        startID++;
        user.setId(id);
        users.put(user.getId(), user);
        log.debug("Данные добавлены для пользователя {}.", user.getId());
        return user;
    }

    public User updateUser(User user) {

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователя с id  " + user.getId() + " не существует");
        }

        users.put(user.getId(), user);
        log.debug("Обновлены данные пользователя {}.", user.getId());
        return user;
    }

    public User findUserById(Integer id) {
        return users.get(id);
    }
}
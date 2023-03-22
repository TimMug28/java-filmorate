package ru.yandex.practicum.filmorate.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import java.util.*;


@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserDAO {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Map<Integer, User> users = new HashMap<>();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<User> getUsersValue() {
        return jdbcTemplate.query("SELECT * FROM users;", new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void createUser(User user) {
        jdbcTemplate.update("INSERT INTO users VALUES (1,?,?,?,?)", user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday());
    }

    @Override
    public User findUserById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?;", new Object[]{id}, new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new ValidationException("User not found with user_id: " + id));
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?,name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }
}



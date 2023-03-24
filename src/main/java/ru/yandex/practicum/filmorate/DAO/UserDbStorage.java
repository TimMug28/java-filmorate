package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.Date;


@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

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

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(" +
                            "email, " +
                            "login, " +
                            "user_name, " +
                            "birthday) " +
                            "VALUES (?, ?, ?, ?)",
                    new String[]{"user_id"});
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);


        user.setId(keyHolder.getKey().intValue());

//        jdbcTemplate.update("INSERT INTO users VALUES (1,?,?,?,?)", user.getEmail(), user.getLogin(), user.getName(),
//                user.getBirthday());
    }

    @Override
    public User findUserById(Integer id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?;", new Object[]{id},
                        new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findAny()
                .orElse(null);
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

    @Override
    public void addToFriend(Integer userId, Integer friendId) {
        if (findUserById(userId).getFriends() != null && findUserById(userId).getFriends().contains(friendId)) {
            throw new ValidationException("пользователь уже добавил " + friendId + " в друзья");
        }
        jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)",
                userId,
                friendId,
                "Подтверждено");
    }

    @Override
    public List<User> getUserFriend(Integer id) {
        return jdbcTemplate.query("SELECT * FROM users " +
                        " WHERE user_id IN " +
                        " (SELECT friend_id FROM friendship WHERE user_id = ? AND status = 'Подтверждено');",
                (rs, rowNum) -> creatingUser(rs), id);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;", userId, friendId);
    }

    @Override
    public List<User> getListOfMutualFriends(Integer id, Integer otherId) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id IN( " +
                        "SELECT DISTINCT(friend_id) FROM friends WHERE user_id = ? AND status = 'Подтверждено' " +
                        " AND friend_id IN (SELECT friend_id FROM friends WHERE user_id = ? AND status = 'Подтверждено')" +
                        " );",
                (rs, rowNum) -> creatingUser(rs), id, otherId);
    }


    private User creatingUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return user;
    }
}



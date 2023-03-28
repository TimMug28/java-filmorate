package ru.yandex.practicum.filmorate.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.sql.Date;


@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users(" +
                            "email, " +
                            "login, " +
                            "user_name, " +
                            "birthday) " +
                            "VALUES (?, ?, ?, ?)",
                    new String[]{"user_id"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        user.setId(id);
        return user;
    }


    @Override
    public Collection<User> getUsersValue() {


        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");


        Collection<User> users = new ArrayList<>();
        while (userRows.next()) {
            User user = new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("user_name"),
                    userRows.getDate("birthday").toLocalDate());
            users.add(user);
        }
        return users;
    }


    @Override
    public User findUserById(Integer id) {
        String sql = "SELECT user_id, email, login, user_name, birthday FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (resultSet, i) ->
                    new User(
                            resultSet.getInt("user_id"),
                            resultSet.getString("email"),
                            resultSet.getString("login"),
                            resultSet.getString("user_name"),
                            resultSet.getDate("birthday").toLocalDate()
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE users SET " +
                        "email = ?, " +
                        "login = ?, " +
                        "user_name = ?, " +
                        "birthday = ? " +
                        "WHERE user_id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public void addToFriend(Integer userId, Integer friendId) {

        jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)",
                userId,
                friendId,
                "Подтверждено");
    }

    @Override
    public List<Integer> getUserFriend(Integer userId) {
        String sql = "SELECT u.* FROM friendship f JOIN users u ON f.friend_id = u.user_id WHERE f.user_id = ?";

        String sqlFriendsByUserId = "SELECT friend_id FROM friendship WHERE user_id = ?";
        List<Integer> friends = jdbcTemplate.query(
                sqlFriendsByUserId, (rs, rowNum) -> rs.getInt("friend_id"), userId);
        return friends;
//
//        List <User> list = jdbcTemplate.query(sql, new Object[]{userId},
//                (resultSet, i) -> new User(
//                        resultSet.getInt("user_id"),
//                        resultSet.getString("email"),
//                        resultSet.getString("login"),
//                        resultSet.getString("user_name"),
//                        resultSet.getDate("birthday").toLocalDate()
//                ));
//
//        return list;
    }


    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?);", userId, friendId, friendId, userId);
    }


    @Override
    public List<User> getListOfMutualFriends(Integer id, Integer otherId) {
        List<User> friendsNames = new ArrayList<>();
        User userById = findUserById(id);
        User otherUserById = findUserById(otherId);
        List<Integer> userFriends = getUserFriend(userById.getId());
        List<Integer> otherUserFriends = getUserFriend(otherUserById.getId());

        for (Integer user : userFriends) {
            if (otherUserFriends.contains(user)) {
                friendsNames.add(findUserById(user));
            }
        }
        return friendsNames;



//        User userById = findUserById(id);
//        User otherUserById = findUserById(otherId);
//        List<User> userFriends = getUserFriend(userById.getId());
//        List<User> otherUserFriends = getUserFriend(otherUserById.getId());
//        for (User user : userFriends) {
//            if (otherUserFriends.contains(user)) {
//                friendsNames.add(user);
//            }
//        }
//        return friendsNames;
    }




    static User creatingUser(ResultSet resultSet, int RowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("user_id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return user;
    }

}



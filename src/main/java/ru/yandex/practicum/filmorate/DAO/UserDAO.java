package ru.yandex.practicum.filmorate.DAO;


import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserDAO {
    public Collection<User> getUsersValue();

    public void createUser(User user);

    public void updateUser(User user);

    public User findUserById(Integer id);

}

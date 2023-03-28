package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    public Collection<User> getUsersValue();

    public User createUser(User user);

    public User updateUser(User user);

    public User findUserById(Integer id);

    public void addToFriend(Integer userId, Integer friendId);

    public List<Integer> getUserFriend (Integer id);

    public void deleteFriend(Integer userId, Integer friendId);

    public List <User> getListOfMutualFriends (Integer id, Integer otherId);
}

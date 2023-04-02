package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.DAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.DAO.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DAOUserTests {
    private final UserDbStorage userStorage;

    @Test
    @DirtiesContext
    void testFindUserById() {
        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("super@mail.ru");
        testUser.setLogin("bob");
        testUser.setName("BigBob");
        testUser.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser);

        Optional<User> userOptional = Optional.ofNullable(userStorage.findUserById(testUser.getId()));

        assertThat(userOptional).isPresent();
        User user = userOptional.get();
        assertThat(user.getId()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testGetUsersValue() {
        User testUser1 = new User();
        testUser1.setId(1);
        testUser1.setEmail("test1@mail.ru");
        testUser1.setLogin("test1");
        testUser1.setName("Test User 1");
        testUser1.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser1);

        User testUser2 = new User();
        testUser2.setId(2);
        testUser2.setEmail("test2@mail.ru");
        testUser2.setLogin("test2");
        testUser2.setName("Test User 2");
        testUser2.setBirthday(LocalDate.of(1995, 2, 2));
        userStorage.createUser(testUser2);

        Collection<User> users = userStorage.getUsersValue();

        assertEquals(2, users.size());
    }

    @Test
    @DirtiesContext
    void testCreateUser() {
        User testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("test");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1994, 1, 1));

        User createdUser = userStorage.createUser(testUser);

        assertThat(createdUser).isEqualToIgnoringGivenFields(testUser, "id");
        assertThat(createdUser.getId()).isNotNull();
    }

    @Test
    @DirtiesContext
    void testUpdateUser() {
        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@mail.ru");
        testUser.setLogin("test");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser);

        testUser.setEmail("updated@mail.ru");
        testUser.setLogin("updated");
        testUser.setName("Updated User");
        testUser.setBirthday(LocalDate.of(1995, 2, 2));

        User updatedUser = userStorage.updateUser(testUser);

        assertThat(updatedUser).isEqualTo(testUser);
    }

    @Test
    @DirtiesContext
    void testAddToFriend() {
        User testUser1 = new User();
        testUser1.setId(1);
        testUser1.setEmail("test1@mail.ru");
        testUser1.setLogin("test1");
        testUser1.setName("Test User 1");
        testUser1.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser1);

        User testUser2 = new User();
        testUser2.setId(2);
        testUser2.setEmail("test2@mail.ru");
        testUser2.setLogin("test2");
        testUser2.setName("Test User 2");
        testUser2.setBirthday(LocalDate.of(1995, 2, 2));
        userStorage.createUser(testUser2);

        userStorage.addToFriend(testUser1.getId(), testUser2.getId());

        List<User> friends = userStorage.getUserFriend(testUser1.getId());
        assertThat(friends.get(0).getId()).isEqualTo(testUser2.getId());
    }

    @Test
    @DirtiesContext
    void testGetUserFriend() {
        User testUser = new User();
        testUser.setId(1);
        testUser.setEmail("super@mail.ru");
        testUser.setLogin("bob");
        testUser.setName("BigBob");
        testUser.setBirthday(LocalDate.of(1994, 1, 1));
        userStorage.createUser(testUser);

        User friend1 = new User();
        friend1.setId(2);
        friend1.setEmail("friend1@mail.ru");
        friend1.setLogin("friend1");
        friend1.setName("Friend1");
        friend1.setBirthday(LocalDate.of(1995, 2, 2));
        userStorage.createUser(friend1);
        userStorage.addToFriend(testUser.getId(), friend1.getId());

        User friend2 = new User();
        friend2.setId(3);
        friend2.setEmail("friend2@mail.ru");
        friend2.setLogin("friend2");
        friend2.setName("Friend2");
        friend2.setBirthday(LocalDate.of(1996, 3, 3));
        userStorage.createUser(friend2);
        userStorage.addToFriend(testUser.getId(), friend2.getId());

        List<User> friends = userStorage.getUserFriend(testUser.getId());

        assertEquals(List.of(friend1, friend2), friends, "Пользователь не найден");
    }
}
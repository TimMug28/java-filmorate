package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
public class User {
    private Long id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends;
    private Set<Integer> likeFilms;


    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.likeFilms = new HashSet<>();
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.likeFilms = new HashSet<>();
    }

    public User(String email, String login, String name, LocalDate birthday, Set <Long> friends, Set <Integer> likeFilms) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
        this.likeFilms = likeFilms;
    }

    public void setFriends (Long id){
        friends.add(id);
    }
    public void deleteFriends(Long id){
        friends.remove(id);
    }

    public void setLikeFilms (Integer filmId){
        likeFilms.add(filmId);
    }
    public void deleteLikeFilm(Integer filmId){
        likeFilms.remove(filmId);
    }
}

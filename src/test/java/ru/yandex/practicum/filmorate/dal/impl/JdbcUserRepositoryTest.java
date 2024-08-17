package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.extractors.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.mappers.*;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcUserRepository.class, JdbcFilmRepository.class, JdbcLikeRepository.class, FilmRowMapper.class,
    UserRowMapper.class, GenreRowMapper.class, FriendshipRowMapper.class, EventRowMapper.class,
    DirectorRowMapper.class, MpaRatingRowMapper.class, FilmResultSetExtractor.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;
    private final JdbcFilmRepository filmRepository;
    private final JdbcLikeRepository likeRepository;

    @Test
    public void testFindUserById() {
        User user1 = userRepository.save(LibraryForCreatingEntities.getUser(1));
        Optional<User> userOptional = userRepository.getById(user1.getId());
        assertThat(userOptional)
            .isPresent()
            .get()
            .usingRecursiveComparison()
            .ignoringExpectedNullFields()
            .isEqualTo(user1);
    }

    @Test
    @DisplayName("Should return user by ID")
    public void should_return_user_by_id() {
        User newUser = new User();
        newUser.setEmail("aleksandrov@email.com");
        newUser.setName("Александр");
        newUser.setBirthday(LocalDate.of(1995, 5, 17));
        newUser.setLogin("aleksandrov");

        Optional<User> userFromRepository = Optional.ofNullable(userRepository.save(newUser));
        assertThat(userFromRepository)
            .isPresent()
            .hasValueSatisfying(user -> assertThat(user).usingRecursiveComparison().isEqualTo(newUser));

        userFromRepository = userRepository.getById(newUser.getId());

        assertThat(userFromRepository)
            .isPresent()
            .hasValueSatisfying(user -> assertThat(user).usingRecursiveComparison().isEqualTo(newUser));
    }

    @Test
    @DisplayName("Should return all users by method")
    public void should_return_all_users() {
        User firstUser = new User();
        firstUser.setId(1);
        firstUser.setEmail("aleksandrov@email.com");
        firstUser.setName("Александр");
        firstUser.setBirthday(LocalDate.of(1995, 5, 17));
        firstUser.setLogin("aleksandrov");

        User secondUser = new User();
        secondUser.setId(2);
        secondUser.setEmail("ivanov@email.com");
        secondUser.setName("Иван");
        secondUser.setBirthday(LocalDate.of(1991, 4, 15));
        secondUser.setLogin("ivanov");

        List<User> localUser = new ArrayList<>();
        localUser.add(firstUser);
        localUser.add(secondUser);

        List<User> repositoryUsers = userRepository.getAll();

        assertThat(repositoryUsers).usingRecursiveComparison().isEqualTo(localUser);
    }

    @Test
    @DisplayName("Should save and return new user")
    public void should_save_and_return_new_user() {
        User newUser = new User();
        newUser.setEmail("maksimov@email.ru");
        newUser.setName("Максим");
        newUser.setLogin("maksimov");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userRepository.save(newUser);

        assertThat(savedUser).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(newUser);

        Optional<User> savedUserById = userRepository.getById(newUser.getId());

        assertThat(savedUserById)
            .isPresent()
            .hasValueSatisfying(user -> {
                assertThat(user).usingRecursiveComparison().isEqualTo(newUser);
            });
    }

    @Test
    @DisplayName("Should update user and return updated user")
    public void should_update_user_and_return_updated_user() {
        Optional<User> repositoryUser = userRepository.getById(1);
        if (repositoryUser.isPresent()) {
            User localUser = repositoryUser.get();
            localUser.setName("Sergey");

            User updatedUser = userRepository.update(localUser);

            repositoryUser = userRepository.getById(updatedUser.getId());

            repositoryUser.ifPresent(user -> assertThat(user)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(localUser));
        }
    }

    @Test
    public void event_user() {
        User user1 = userRepository.save(LibraryForCreatingEntities.getUser(1));
        User user2 = userRepository.save(LibraryForCreatingEntities.getUser(2));

        userRepository.eventFriend(user1.getId(), user2.getId(), OperationType.ADD);
        userRepository.eventFriend(user1.getId(), user2.getId(), OperationType.UPDATE);
        userRepository.eventFriend(user1.getId(), user2.getId(), OperationType.REMOVE);
        userRepository.eventFriend(user2.getId(), user1.getId(), OperationType.ADD);

        List<Event> eventList1 = userRepository.getUserEvents(user1.getId());
        List<Event> eventList2 = userRepository.getUserEvents(user2.getId());
        assertEquals(3, eventList1.size(), "Ожидается 3 события");
        assertEquals(EventType.FRIEND, eventList1.get(0).getEventType(), "Ожидается событие Дружбы");
        assertEquals(OperationType.ADD, eventList1.get(0).getOperation(), "Ожидается действие ADD");
        assertEquals(OperationType.UPDATE, eventList1.get(1).getOperation(), "Ожидается действие UPDATE");
        assertEquals(OperationType.REMOVE, eventList1.get(2).getOperation(), "Ожидается действие REMOVE");


        assertEquals(1, eventList2.size(), "Ожидается 1 события");
        assertEquals(EventType.FRIEND, eventList2.get(0).getEventType(), "Ожидается событие Отзыв");
        assertEquals(OperationType.ADD, eventList2.get(0).getOperation(), "Ожидается действие ADD");

        userRepository.delete(user1.getId());
        eventList1 = userRepository.getUserEvents(user1.getId());
        eventList2 = userRepository.getUserEvents(user2.getId());
        assertEquals(0, eventList1.size(), "Ожидается отсутствие событий");
        assertEquals(1, eventList2.size(), "Ожидается 1 события");

        Film film1 = filmRepository.save(LibraryForCreatingEntities.getFilm(1));
        Film film2 = filmRepository.save(LibraryForCreatingEntities.getFilm(2));

        likeRepository.eventLike(film1.getId(), user2.getId(), OperationType.ADD);
        likeRepository.eventLike(film1.getId(), user2.getId(), OperationType.UPDATE);
        likeRepository.eventLike(film1.getId(), user2.getId(), OperationType.REMOVE);

        eventList1 = userRepository.getUserEvents(user1.getId());
        eventList2 = userRepository.getUserEvents(user2.getId());
        assertEquals(0, eventList1.size(), "Ожидается отсутствие событий");
        assertEquals(4, eventList2.size(), "Ожидается 4 события");

        assertEquals(EventType.FRIEND, eventList2.get(0).getEventType(), "Ожидается событие Отзыв");
        assertEquals(OperationType.ADD, eventList2.get(0).getOperation(), "Ожидается действие ADD");
        assertEquals(EventType.LIKE, eventList2.get(1).getEventType(), "Ожидается событие Лайк");
        assertEquals(OperationType.ADD, eventList2.get(1).getOperation(), "Ожидается действие ADD");
        assertEquals(EventType.LIKE, eventList2.get(2).getEventType(), "Ожидается событие Лайк");
        assertEquals(OperationType.UPDATE, eventList2.get(2).getOperation(), "Ожидается действие UPDATE");
        assertEquals(EventType.LIKE, eventList2.get(3).getEventType(), "Ожидается событие Лайк");
        assertEquals(OperationType.REMOVE, eventList2.get(3).getOperation(), "Ожидается действие REMOVE");
    }
}

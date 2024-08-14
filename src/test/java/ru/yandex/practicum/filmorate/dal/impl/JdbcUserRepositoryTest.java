package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.mappers.*;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcUserRepository.class, JdbcFilmRepository.class, JdbcLikeRepository.class, FilmRowMapper.class,
        UserRowMapper.class, GenreRowMapper.class, FriendshipRowMapper.class, EventRowMapper.class,
        DirectorRowMapper.class, MpaRatingRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;
    private final JdbcFilmRepository filmRepository;
    private final JdbcLikeRepository likeRepository;

    @Test
    public void testFindUserById() {
        User user1 = userRepository.save(LibraryForCreatingEntities.getUser(1));
        Optional<User> userOptional = userRepository.findById(user1.getId());
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(user1);
    }

    @Test
    public void eventUser() {
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

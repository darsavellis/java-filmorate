package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcUserRepository.class, JdbcFilmRepository.class, JdbcLikeRepository.class, FilmRowMapper.class,
        UserRowMapper.class, GenreRowMapper.class, FriendshipRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;

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
}

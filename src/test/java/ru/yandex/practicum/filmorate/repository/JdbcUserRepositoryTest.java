package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcUserRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({JdbcUserRepository.class, UserRowMapper.class,FriendshipRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcUserRepositoryTest {
    public static final long TEST_USER_ID = 1L;
    private final JdbcUserRepository userRepository;

    static User getUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setEmail("email@email.com");
        return user;
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userRepository.findById(TEST_USER_ID);

        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(getUser());
    }
}

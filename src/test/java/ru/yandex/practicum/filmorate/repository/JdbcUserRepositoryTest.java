package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcUserRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({JdbcUserRepository.class, UserRowMapper.class, FriendshipRowMapper.class, EventRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcUserRepositoryTest {
    private final JdbcUserRepository userRepository;


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

        userFromRepository = userRepository.findById(newUser.getId());

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

        userRepository.save(firstUser);
        userRepository.save(secondUser);

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

        Optional<User> savedUserById = userRepository.findById(newUser.getId());

        assertThat(savedUserById)
            .isPresent()
            .hasValueSatisfying(user -> {
                assertThat(user).usingRecursiveComparison().isEqualTo(newUser);
            });
    }

    @Test
    @DisplayName("Should update user and return updated user")
    public void should_update_user_and_return_updated_user() {
        Optional<User> repositoryUser = userRepository.findById(1);
        if (repositoryUser.isPresent()) {
            User localUser = repositoryUser.get();
            localUser.setName("Sergey");

            User updatedUser = userRepository.update(localUser);

            repositoryUser = userRepository.findById(updatedUser.getId());

            repositoryUser.ifPresent(user -> assertThat(user)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(localUser));
        }
    }
}

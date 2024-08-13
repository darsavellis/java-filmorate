package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({JdbcFilmRepository.class, FilmRowMapper.class, GenreRowMapper.class,
    MpaRatingRowMapper.class, DirectorRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository filmRepository;


    @Test
    @DisplayName("Should return film by ID")
    public void should_return_film_by_id() {
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("Интерстеллар");
        newFilm.setDescription("Описание интерстеллара");
        newFilm.setReleaseDate(LocalDate.of(2014, 11, 6));
        newFilm.setDuration(169);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(3);
        newFilm.setMpa(mpaRating);

        Optional<Film> filmOptional = filmRepository.getById(newFilm.getId());

        assertThat(filmOptional)
            .isPresent()
            .hasValueSatisfying(film -> {
                assertThat(film).usingRecursiveComparison().isEqualTo(newFilm);
            });
    }

    @Test
    @DisplayName("Should return all films by method")
    public void should_return_all_films() {
        Film firstFilm = new Film();
        firstFilm.setId(1);
        firstFilm.setName("Интерстеллар");
        firstFilm.setDescription("Описание интерстеллара");
        firstFilm.setReleaseDate(LocalDate.of(2014, 11, 6));
        firstFilm.setDuration(169);
        MpaRating fistMpaRating = new MpaRating();
        fistMpaRating.setId(3);
        firstFilm.setMpa(fistMpaRating);

        Film secondFilm = new Film();
        secondFilm.setId(2);
        secondFilm.setName("1 + 1");
        secondFilm.setDescription("Описание 1 + 1");
        secondFilm.setReleaseDate(LocalDate.of(2011, 9, 23));
        secondFilm.setDuration(112);
        MpaRating secondMpaRating = new MpaRating();
        secondMpaRating.setId(4);
        secondFilm.setMpa(secondMpaRating);

        List<Film> localFilm = new ArrayList<>();
        localFilm.add(firstFilm);
        localFilm.add(secondFilm);

        List<Film> repositoryFilm = filmRepository.getAll();

        assertThat(repositoryFilm).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(localFilm);
    }

    @Test
    @DisplayName("Should save and return new film")
    public void should_save_and_return_new_film() {
        Film newFilm = new Film();
        newFilm.setId(1);
        newFilm.setName("Интерстеллар");
        newFilm.setDescription("Описание интерстеллара");
        newFilm.setReleaseDate(LocalDate.of(2014, 11, 6));
        newFilm.setDuration(112);
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(4);
        newFilm.setMpa(mpaRating);

        Film savedFilm = filmRepository.save(newFilm);

        assertThat(savedFilm).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(newFilm);

        Optional<Film> savedFilmById = filmRepository.getById(newFilm.getId());

        assertThat(savedFilmById)
            .isPresent()
            .hasValueSatisfying(film -> {
                assertThat(film).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(newFilm);
            });
    }

    @Test
    @DisplayName("Should update film and return updated film")
    public void should_update_film_and_return_updated_film() {
        Optional<Film> repositoryFilm = filmRepository.getById(1);
        if (repositoryFilm.isPresent()) {
            Film locaFilm = repositoryFilm.get();
            locaFilm.setName("1 + 1 (2011)");

            Film updatedFilm = filmRepository.update(locaFilm);

            repositoryFilm = filmRepository.getById(updatedFilm.getId());

            repositoryFilm.ifPresent(film -> {
                assertThat(film)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(locaFilm);
            });
        }
    }
}

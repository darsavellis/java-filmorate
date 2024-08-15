package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseGenreService implements GenreService {
    static final String GENRE_ID_NOT_FOUND = "Genre ID=%s not found";
    final GenreRepository genreRepository;

    @Override
    public Collection<Genre> getGenres() {
        return genreRepository.getGenres();
    }

    @Override
    public Genre getGenreById(long genreId) {
        return genreRepository.getGenreById(genreId)
            .orElseThrow(() -> new NotFoundException(String.format(GENRE_ID_NOT_FOUND, genreId)));
    }
}

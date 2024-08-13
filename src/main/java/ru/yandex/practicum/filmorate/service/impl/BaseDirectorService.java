package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.impl.JdbcDirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BaseDirectorService implements DirectorService {
    static final String DIRECTOR_ID_NOT_FOUND = "Director ID=%s not found";

    final JdbcDirectorRepository directorRepository;

    @Override
    public Collection<Director> getDirectors() {
        return directorRepository.getAll();
    }

    @Override
    public Director getDirectorById(long directorId) {
        return directorRepository.getById(directorId)
            .orElseThrow(() -> new NotFoundException(String.format(DIRECTOR_ID_NOT_FOUND, directorId)));
    }

    @Override
    public Director createDirector(Director director) {
        return directorRepository.save(director);
    }

    @Override
    public Director updateDirector(Director newDirector) {


        directorRepository.getById(newDirector.getId())
            .orElseThrow(() -> new NotFoundException(String.format(DIRECTOR_ID_NOT_FOUND, newDirector.getId())));

        return directorRepository.update(newDirector);
    }

    @Override
    public Director deleteDirector(long directorId) {
        Director director = getDirectorById(directorId);
        directorRepository.delete(directorId);
        return director;
    }
}

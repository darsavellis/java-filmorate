package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.OperationType;

import java.util.Set;

public interface LikeRepository {
    Set<Long> getLikesByFilmId(long filmId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    void eventLike(long filmId, long userId, OperationType operationType);
}

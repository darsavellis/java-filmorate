package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.OperationType;

public interface LikeRepository {
    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    void eventLike(long filmId, long userId, OperationType operationType);
}

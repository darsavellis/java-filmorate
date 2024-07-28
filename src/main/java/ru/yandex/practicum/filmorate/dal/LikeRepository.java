package ru.yandex.practicum.filmorate.dal;

import java.util.Set;

public interface LikeRepository {
    Set<Long> getLikesByFilmId(long filmId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

}

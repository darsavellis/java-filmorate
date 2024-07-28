package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRatingRepository {
    List<MpaRating> getMpaRatings();

    Optional<MpaRating> getById(long mpaRatingId);
}

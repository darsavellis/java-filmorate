package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseMpaRatingService implements MpaRatingService {
    static final String MPA_RATING_ID_NOT_VALID = "MpaRating ID=%s not valid";
    final MpaRatingRepository mpaRatingRepository;

    @Override
    public List<MpaRating> getMpaRatings() {
        return mpaRatingRepository.getMpaRatings();
    }

    @Override
    public MpaRating getMpaRatingById(long mpaRatingId) {
        return mpaRatingRepository.getById(mpaRatingId)
            .orElseThrow(() -> new NotFoundException(String.format(MPA_RATING_ID_NOT_VALID, mpaRatingId)));
    }
}

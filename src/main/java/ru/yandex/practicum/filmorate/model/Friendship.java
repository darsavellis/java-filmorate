package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Friendship {
    long id;
    long senderId;
    long receiverId;
    long statusId;
}

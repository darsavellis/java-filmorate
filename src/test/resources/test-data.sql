INSERT INTO PUBLIC.RATINGS
(NAME, DESCRIPTION)
VALUES('G', 'Фильм демонстрируется без ограничений.');
INSERT INTO PUBLIC.RATINGS
(NAME, DESCRIPTION)
VALUES('PG', 'Детям рекомендуется смотреть фильм с родителями.');
INSERT INTO PUBLIC.RATINGS
(NAME, DESCRIPTION)
VALUES('PG-13', 'Просмотр не желателен детям до 13 лет.');
INSERT INTO PUBLIC.RATINGS
(NAME, DESCRIPTION)
VALUES('R', 'Лица, не достигшие 17-летнего возраста, допускаются на фильм только в сопровождении одного из родителей, либо законного представителя.');
INSERT INTO PUBLIC.RATINGS
(NAME, DESCRIPTION)
VALUES('NC-17', 'Лица 17-летнего возраста и младше на фильм не допускаются.');


INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Комедия');
INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Драма');
INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Мультфильм');
INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Триллер');
INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Документальный');
INSERT INTO PUBLIC.GENRES
(NAME)
VALUES('Боевик');

INSERT INTO PUBLIC.FRIEND_STATUSES
(STATUS)
VALUES('PENDING');
INSERT INTO PUBLIC.FRIEND_STATUSES
(STATUS)
VALUES('ACCEPTED');

INSERT INTO PUBLIC.USERS
(EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES('ivanov@email.com', 'diamond', 'Ivan', '1998-10-20');

INSERT INTO users (name, email, login, birthday) VALUES ('John', 'email@dog.com', 'frog', '2002-12-12');
INSERT INTO users (name, email, login, birthday) VALUES ('John1', 'email@dog1.com', 'frog1', '2002-12-11');
INSERT INTO users (name, email, login, birthday) VALUES ('John2', 'email@dog2.com', 'frog2', '2002-12-10');

UPDATE users SET name = 'John3', email = 'email@dog3.com', login = 'frog3', birthday = '2001-12-14'
WHERE id = 3;

INSERT INTO FRIENDSHIPS (FIRST_USER_ID, SECOND_USER_ID, STATUS_ID) VALUES (1, 2, 1);
INSERT INTO FRIENDSHIPS (FIRST_USER_ID, SECOND_USER_ID, STATUS_ID) VALUES (1, 3, 2);

DELETE FROM FRIENDSHIPS WHERE FIRST_USER_ID = 1 AND SECOND_USER_ID = 2;

INSERT INTO films (name, description, duration, release_Date, RATING_ID)
VALUES ('New film', 'This film is about ...', 120, '2017-12-28', 1);

INSERT INTO films (name, description, duration, release_Date, RATING_ID)
VALUES ('New film1', 'This film is about ...1', 121, '2017-12-29', 2);

INSERT INTO films (name, description, duration, release_Date, RATING_ID)
VALUES ('New film2', 'This film is about ...2', 122, '2017-12-30', 1);

INSERT INTO films (name, description, duration, release_Date, RATING_ID)
VALUES ('New film1', 'This film is about ...1', 121, '2017-12-29', 2);

UPDATE films SET name = 'updated', description = 'updated', duration = 200,
release_Date = '2020-10-20', RATING_ID = 2 WHERE id = 3;

MERGE INTO likes (id, film_id, user_id) VALUES (1, 1, 1);
MERGE INTO likes (id, film_id, user_id) VALUES (2, 1, 2);
MERGE INTO likes (id, film_id, user_id) VALUES (3, 1, 3);

MERGE INTO likes (id, film_id, user_id) VALUES (4, 2, 1);
MERGE INTO likes (id, film_id, user_id) VALUES (5, 2, 2);
MERGE INTO likes (id, film_id, user_id) VALUES (6, 2, 3);

DELETE FROM likes WHERE film_id = 1 AND user_id = 3;

INSERT INTO film_genre (film_id, genre_id) VALUES (1, 1);
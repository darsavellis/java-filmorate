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
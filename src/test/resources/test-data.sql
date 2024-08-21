MERGE INTO RATINGS
(ID, NAME, DESCRIPTION)
VALUES  (1, 'G', 'Фильм демонстрируется без ограничений.'),
        (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями.'),
        (3, 'PG-13', 'Просмотр не желателен детям до 13 лет.'),
        (4, 'R', 'Лица, не достигшие 17-летнего возраста, допускаются на фильм только в сопровождении одного из
        родителей, либо законного представителя.'),
        (5, 'NC-17', 'Лица 17-летнего возраста и младше на фильм не допускаются.');

MERGE INTO GENRES
(ID, NAME)
VALUES  (1, 'Комедия'),
        (2, 'Драма'),
        (3, 'Мультфильм'),
        (4, 'Триллер'),
        (5, 'Документальный'),
        (6, 'Боевик');

MERGE INTO EVENT_TYPES (ID, NAME)
VALUES
              (1, 'LIKE'),
			  (2, 'REVIEW'),
			  (3, 'FRIEND');
MERGE INTO OPERATION_TYPES (ID, NAME)
VALUES
              (1, 'REMOVE'),
			  (2, 'ADD'),
			  (3, 'UPDATE');

MERGE INTO FILMS
(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES  (1, 'Интерстеллар', 'Описание интерстеллара', '2014-11-06', '169', 3),
        (2, '1 + 1', 'Описание 1 + 1', '2011-09-23', '112', 4);

MERGE INTO USERS
(ID, EMAIL, LOGIN, NAME, SURNAME, BIRTHDAY)
VALUES  (1, 'aleksandrov@email.com', 'aleksandrov', 'Александр', 'Иванов', '1995-05-17'),
        (2, 'ivanov@email.com', 'ivanov', 'Иван', 'Александров', '1991-04-15');

ALTER TABLE USERS ALTER COLUMN ID RESTART WITH (SELECT MAX(ID) + 1 FROM USERS);
ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH (SELECT MAX(ID) + 1 FROM FILMS);

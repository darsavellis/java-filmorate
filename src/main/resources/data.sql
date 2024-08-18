MERGE INTO PUBLIC.RATINGS (ID, NAME, DESCRIPTION)
VALUES
              (1, 'G', 'для всех возрастов'),
              (2, 'PG', 'для всех, но маленьким детям рекомендуется просмотр с родителями'),
              (3, 'PG-13', ' детям до 13 лет просмотр не рекомендуется'),
              (4, 'R', 'до 17 лет просмотр исключительно с родителями'),
              (5, 'NC-17', 'просмотр только после 17 лет');
MERGE INTO PUBLIC.GENRES (ID, NAME)
VALUES
              (1, 'Комедия'),
              (2, 'Драма'),
              (3, 'Мультфильм'),
              (4, 'Триллер'),
              (5, 'Документальный'),
              (6, 'Боевик');
MERGE INTO PUBLIC.EVENT_TYPES (ID, NAME)
VALUES
              (1, 'LIKE'),
			  (2, 'REVIEW'),
			  (3, 'FRIEND');
MERGE INTO PUBLIC.OPERATION_TYPES (ID, NAME)
VALUES
              (1, 'REMOVE'),
			  (2, 'ADD'),
			  (3, 'UPDATE');

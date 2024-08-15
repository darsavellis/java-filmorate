CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	EMAIL VARCHAR_IGNORECASE(255) NOT NULL,
	LOGIN VARCHAR_IGNORECASE(255) NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	SURNAME VARCHAR(255),
	BIRTHDAY TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.DIRECTORS (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	NAME VARCHAR(255) NOT NULL,
	SURNAME VARCHAR(255),
	BIRTHDAY TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS PUBLIC.RATINGS (
	ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	NAME VARCHAR(255) NOT NULL,
	DESCRIPTION VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	NAME VARCHAR(255) NOT NULL,
	DESCRIPTION VARCHAR(255) NOT NULL,
	RELEASE_DATE TIMESTAMP WITH TIME ZONE NOT NULL,
	DURATION BIGINT NOT NULL,
	RATING_ID INTEGER NOT NULL,
	CONSTRAINT FILMS_RATINGS_FK FOREIGN KEY (RATING_ID) REFERENCES PUBLIC.RATINGS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_DIRECTOR (
	FILM_ID BIGINT NOT NULL,
	DIRECTOR_ID BIGINT NOT NULL,
	CONSTRAINT FILM_DIRECTOR_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID),
	CONSTRAINT FILM_DIRECTOR_DIRECTORS_FK FOREIGN KEY (DIRECTOR_ID) REFERENCES PUBLIC.DIRECTORS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES (
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_GENRE (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    FILM_ID BIGINT NOT NULL,
    GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID) ON DELETE CASCADE,
	CONSTRAINT FILM_GENRE_GENRES_FK FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRES(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.LIKES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    FILM_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
	CONSTRAINT LIKES_USERS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE,
	CONSTRAINT LIKES_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.FRIEND_STATUSES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    STATUS VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDSHIPS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    FIRST_USER_ID BIGINT NOT NULL,
    SECOND_USER_ID BIGINT NOT NULL,
    STATUS_ID INTEGER NOT NULL,
	CONSTRAINT FRIENDSHIPS_USERS_FK_1 FOREIGN KEY (FIRST_USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE,
	CONSTRAINT FRIENDSHIPS_USERS_FK_2 FOREIGN KEY (SECOND_USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE,
    CONSTRAINT FRIENDSHIPS_FRIEND_STATUSES_FK FOREIGN KEY (STATUS_ID) REFERENCES PUBLIC.FRIEND_STATUSES(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.EVENT_TYPES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.OPERATION_TYPES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS PUBLIC.EVENTS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    USER_ID BIGINT NOT NULL,
    TYPE_ID BIGINT NOT NULL,
	OPERATION_ID BIGINT NOT NULL,
    ENTITY_ID BIGINT NOT NULL,
	TIMESTAMP TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT EVENTS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE,
	CONSTRAINT EVENTS_TYPES_FK FOREIGN KEY (TYPE_ID) REFERENCES PUBLIC.EVENT_TYPES(ID) ON DELETE CASCADE,
	CONSTRAINT EVENTS_OPERATIONS_FK FOREIGN KEY (OPERATION_ID) REFERENCES PUBLIC.OPERATION_TYPES(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.REVIEWS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	CONTENT VARCHAR(512) NOT NULL,
	IS_POSITIVE BOOL NOT NULL,
    USER_ID BIGINT NOT NULL,
	FILM_ID BIGINT NOT NULL,
	TIMESTAMP TIMESTAMP WITH TIME ZONE,
	CONSTRAINT REVIEWS_USER_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE,
	CONSTRAINT REVIEWS_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PUBLIC.REVIEW_USER (
    REVIEW_ID BIGINT REFERENCES PUBLIC.REVIEWS(ID) ON DELETE CASCADE NOT NULL,
    USER_ID BIGINT REFERENCES PUBLIC.USERS(ID) ON DELETE CASCADE NOT NULL,
    IS_LIKE BOOL NOT NULL,
    PRIMARY KEY (REVIEW_ID, USER_ID)
);

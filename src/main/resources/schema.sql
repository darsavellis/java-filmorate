CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
	ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	EMAIL VARCHAR_IGNORECASE(255) NOT NULL,
	LOGIN VARCHAR_IGNORECASE(255) NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	BIRTHDAY TIMESTAMP WITH TIME ZONE NOT NULL
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
	CONSTRAINT FILMS_RATINGS_FK FOREIGN KEY (RATING_ID) REFERENCES PUBLIC.RATINGS(ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES (
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM_GENRE (
	ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    FILM_ID BIGINT NOT NULL,
    GENRE_ID INTEGER NOT NULL,
	CONSTRAINT FILM_GENRE_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID),
	CONSTRAINT FILM_GENRE_GENRES_FK FOREIGN KEY (GENRE_ID) REFERENCES PUBLIC.GENRES(ID)
);

CREATE TABLE IF NOT EXISTS LIKES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    FILM_ID BIGINT NOT NULL,
    USER_ID BIGINT NOT NULL,
	CONSTRAINT LIKES_USERS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(ID),
	CONSTRAINT LIKES_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(ID)
);

CREATE TABLE IF NOT EXISTS FRIEND_STATUSES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    STATUS VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS FRIENDSHIPS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    FIRST_USER_ID BIGINT NOT NULL,
    SECOND_USER_ID BIGINT NOT NULL,
    STATUS_ID INTEGER NOT NULL,
	CONSTRAINT FRIENDSHIPS_USERS_FK_1 FOREIGN KEY (FIRST_USER_ID) REFERENCES PUBLIC.USERS(ID),
	CONSTRAINT FRIENDSHIPS_USERS_FK_2 FOREIGN KEY (SECOND_USER_ID) REFERENCES PUBLIC.USERS(ID),
    CONSTRAINT FRIENDSHIPS_FRIEND_STATUSES_FK FOREIGN KEY (STATUS_ID) REFERENCES PUBLIC.FRIEND_STATUSES(ID)
);

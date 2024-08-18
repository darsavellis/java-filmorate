package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse notFoundExceptionHandler(final NotFoundException exception) {
        log.info(exception.getMessage(), exception);
        return new ErrorResponse("error", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse notFoundExceptionHandler(final DataAccessException exception) {
        log.info(exception.getMessage(), exception);
        return new ErrorResponse("error", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse badRequest(final ValidationException exception) {
        log.info(exception.getMessage(), exception);
        return new ErrorResponse("error", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse internalServerExceptionHandler(final Exception exception) {
        log.warn(Arrays.toString(exception.getStackTrace()));
        return new ErrorResponse("", exception.getMessage());
    }
}

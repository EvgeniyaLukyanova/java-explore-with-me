package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.exception.dto.ApiError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class ApiErrorHandler {
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleIncorrectParameterException(final MethodArgumentNotValidException e) {
        log.warn("Http Error Code : 400: {}", e.getMessage());
        List<String> stackTrace = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        return new ApiError(stackTrace, e.getMessage(), BAD_REQUEST, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ApiError handleDataIntegrityViolationException(final ConstraintViolationException e) {
        log.warn("Http Error Code : 409: {}", e.getMessage());
        List<String> stackTrace = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        return new ApiError(null, e.getMessage(), CONFLICT, "Было нарушено ограничение целостности.", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ApiError handleDataIntegrityConstraintException(final IntegrityConstraintException e) {
        log.warn("Http Error Code : 409: {}", e.getMessage());
        List<String> stackTrace = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        return new ApiError(null, e.getMessage(), CONFLICT, "Было нарушено ограничение целостности.", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("Http Error Code : 404: {}", e.getMessage());
        List<String> stackTrace = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        return new ApiError(null, e.getMessage(), NOT_FOUND, "Требуемый объект не найден.", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn("Http Error Code : 400: {}", e.getMessage());
        List<String> stackTrace = Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.toList());
        return new ApiError(null, e.getMessage(), BAD_REQUEST, "Неправильно составленный запрос.", LocalDateTime.now());
    }
}

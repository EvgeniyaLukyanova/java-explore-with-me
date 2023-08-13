package ru.practicum.ewm.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@Data
public class ApiError {
    private final List<String> errors;
    private final String message;
    private final HttpStatus status;
    private final String reason;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private final LocalDateTime timestamp;
}

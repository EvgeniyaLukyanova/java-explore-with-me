package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;

import static ru.practicum.constants.Constants.DATE_FORMAT;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDateTime timestamp;
}

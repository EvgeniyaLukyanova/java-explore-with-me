package ru.practicum.statistic.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.statistic.exception.ValidationException;
import ru.practicum.statistic.service.StatisticService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@RestController
@Slf4j
@AllArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStatistic(@RequestBody StatDto stat) {
        log.info("Сохранение информации о запросе: {}", stat);
        statisticService.createStatistic(stat);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ViewStatDto> getStatistics(@RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                                 @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                                 @RequestParam(required = false) String[] uris,
                                                 @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Получение статистики по посещениям");
        return statisticService.getStatistics(start, end, uris, unique);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidData(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }
}

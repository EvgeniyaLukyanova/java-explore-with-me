package ru.practicum.statistic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.statistic.service.StatisticService;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStatistic(@RequestBody StatDto stat) {
        log.info("Сохранение информации о запросе: {}", stat);
        statisticService.createStatistic(stat);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ViewStatDto> getStatistics(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                 @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                 @RequestParam(required = false) String[] uris,
                                                 @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Получение статистики по посещениям");
        return statisticService.getStatistics(start, end, uris, unique);
    }
}

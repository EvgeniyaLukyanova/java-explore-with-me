package ru.practicum.statistic.service;

import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    void createStatistic(StatDto stat);

    List<ViewStatDto> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);
}

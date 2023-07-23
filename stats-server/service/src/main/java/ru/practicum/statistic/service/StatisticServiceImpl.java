package ru.practicum.statistic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.statistic.mapper.StatisticMapper;
import ru.practicum.statistic.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository repository;

    @Autowired
    public StatisticServiceImpl(StatisticRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public void createStatistic(StatDto stat) {
        repository.save(StatisticMapper.toStatistic(stat));
    }

    @Override
    public List<ViewStatDto> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        List<String> uriList = uris != null ? List.of(uris) : new ArrayList<String>(Arrays.asList(""));
        if (unique) {
            return repository.getDistinctViewStat(start, end, uriList);
        } else {
            return repository.getViewStat(start, end, uriList);
        }
    }
}

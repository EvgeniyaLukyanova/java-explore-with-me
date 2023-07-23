package ru.practicum.statistic.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatDto;
import ru.practicum.statistic.model.Statistic;

@UtilityClass
public class StatisticMapper {
    public Statistic toStatistic(StatDto stat) {
        if (stat != null) {
            Statistic statistic = new Statistic();
            statistic.setApp(stat.getApp());
            statistic.setUri(stat.getUri());
            statistic.setIp(stat.getIp());
            statistic.setRequesTime(stat.getTimestamp());
            return statistic;
        } else {
            return null;
        }
    }
}

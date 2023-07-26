package ru.practicum.statistic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.StatDto;
import ru.practicum.statistic.model.Statistic;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatisticMapper {
    @Mapping(source = "timestamp", target = "requesTime", dateFormat = DATE_FORMAT)
    Statistic statDtoToStatistic(StatDto statDto);
}

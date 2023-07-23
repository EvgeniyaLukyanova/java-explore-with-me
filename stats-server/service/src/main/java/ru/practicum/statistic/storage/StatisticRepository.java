package ru.practicum.statistic.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.statistic.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    @Query("select new ru.practicum.dto.ViewStatDto(s.app, s.uri, count(s.ip) as cnt) " +
            "from Statistic as s " +
            "where (coalesce(:uris) = '' or s.uri in (:uris)) " +
            "  and s.requesTime between :start and :end " +
            "group by s.app, s.uri " +
            "order by cnt desc")
    List<ViewStatDto> getViewStat(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("uris") List<String> uris);
    @Query("select new ru.practicum.dto.ViewStatDto(s.app, s.uri, count(distinct s.ip) as cnt) " +
            "from Statistic as s " +
            "where (coalesce(:uris) = '' or s.uri in (:uris)) " +
            "  and s.requesTime between :start and :end " +
            "group by s.app, s.uri " +
            "order by cnt desc")
    List<ViewStatDto> getDistinctViewStat(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);
}


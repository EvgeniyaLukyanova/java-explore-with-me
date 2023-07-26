package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@Service
public class StatisticClient extends BaseClient {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Autowired
    public StatisticClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createStatistic(StatDto stat) {
        return post("/hit", stat);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(formatter),
                "end", end.format(formatter),
                "uris", uris,
                "unique", unique);
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}

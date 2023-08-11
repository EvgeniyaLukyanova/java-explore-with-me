package ru.practicum.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.Map;

import static ru.practicum.constants.Constants.FORMATTER;

public class StatisticClient extends BaseClient {

    public StatisticClient(String serverUrl, RestTemplateBuilder builder) {
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
                "start", start.format(FORMATTER),
                "end", end.format(FORMATTER),
                "uris", uris,
                "unique", unique);
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}

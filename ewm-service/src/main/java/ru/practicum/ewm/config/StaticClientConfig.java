package ru.practicum.ewm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.StatisticClient;

@Configuration
public class StaticClientConfig {
    @Value("${ewm-server.url}")
    private String serverUrl;

    @Bean
    public StatisticClient statisticClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new StatisticClient(serverUrl, builder);
    }
}

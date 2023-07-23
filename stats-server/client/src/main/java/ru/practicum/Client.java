package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Client {

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);

//        RestTemplateBuilder builder = new RestTemplateBuilder();
//
//        StatisticClient statisticClient = new StatisticClient("http://localhost:9090", builder);
//
//        StatDto stat = new StatDto("ewm-main-service", "/events", "121.0.0.1", LocalDateTime.now());
//        ResponseEntity<Object> response1 = statisticClient.createStatistic(stat);
//
//        ResponseEntity<Object> response2 = statisticClient.getStatistics(LocalDateTime.now().minusHours(1),
//                LocalDateTime.now().plusHours(1),
//                new String[]{"/events"},
//                false);
    }
}

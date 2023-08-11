package ru.practicum.statistic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "app")
    private String app;
    @NotNull
    @Column(name = "uri")
    private String uri;
    @NotNull
    @Column(name = "ip")
    private String ip;
    @NotNull
    @Column(name = "request_time")
    private LocalDateTime requesTime;
}

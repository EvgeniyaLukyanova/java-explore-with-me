package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Slf4j
public class EventControllerIsClosed {
    private final EventService eventService;

    public EventControllerIsClosed(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEvents(@PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение списка всех событий пользователя {}", userId);
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto event) {
        log.info("Добавление события {} пользователем с ид {}", event, userId);
        return eventService.createEvent(userId, event);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("Получение полного списка о событии {} пользователя {}", eventId, userId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest event) {
        log.info("Изменяем события с ид {} пользователя с ид {}", eventId, userId);
        return eventService.updateEvent(userId, eventId, event);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("Получение списка запросов на участие в событии {} пользователя {}", eventId, userId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult requestStatusUpdate(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest requestStatus) {
        log.info("Изменение статуса заявок на {} в событиях {}, пользователя с ид {}",
                requestStatus.getStatus(), requestStatus.getRequestIds().toString(), userId);
        return eventService.requestStatusUpdate(userId, eventId, requestStatus);
    }
}

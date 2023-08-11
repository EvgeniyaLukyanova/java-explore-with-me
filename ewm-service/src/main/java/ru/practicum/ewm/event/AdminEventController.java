package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.reference.EventState;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
public class AdminEventController {
    private final EventService eventService;

    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventFullDto> getAdminEvents(@RequestParam(name = "users", required = false) List<Long> users,
                                                   @RequestParam(name = "states", required = false) List<EventState> states,
                                                   @RequestParam(name = "categories", required = false) List<Long> categories,
                                                   @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                                   @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение списка событий администратором");
        return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateAdminEvent(@PathVariable Long eventId,
                                         @RequestBody @Valid UpdateEventAdminRequest event) {
        log.info("Изменяем события с ид {} администратором", eventId);
        return eventService.updateAdminEvent(eventId, event);
    }
}

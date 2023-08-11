package ru.practicum.ewm.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.reference.SortingOptions;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@RestController
@RequestMapping(path = "/events")
@Slf4j
public class EventControllerIsPublic {
    private final EventService eventService;

    public EventControllerIsPublic(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getPublicEvents(@RequestParam(name = "text", required = false) String text,
                                                     @RequestParam(name = "categories", required = false) List<Long> categories,
                                                     @RequestParam(name = "paid", required = false) Boolean paid,
                                                     @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                                     @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                                     @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                     @RequestParam(name = "sort", required = false) SortingOptions sort,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                     HttpServletRequest request) {
        log.info("Получение списка событий публичным API");
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPublicEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получение события с ид = {}", id);
        return eventService.getPublicEventById(id, request);
    }
}

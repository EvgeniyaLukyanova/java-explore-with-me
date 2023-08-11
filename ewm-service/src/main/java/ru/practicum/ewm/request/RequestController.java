package ru.practicum.ewm.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Пользователь с ид {} добавляет запрос к событию с ид {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto requestCanceled(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Пользователь с ид {} отменяет запрос с ид {}", userId, requestId);
        return requestService.requestCanceled(userId, requestId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getRequest(@PathVariable Long userId) {
        log.info("Получение списка заявок пользователя с ид {}", userId);
        return requestService.getRequest(userId);
    }
}

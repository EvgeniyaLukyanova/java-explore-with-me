package ru.practicum.ewm.event.service;

import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.reference.EventState;
import ru.practicum.ewm.event.reference.SortingOptions;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long id, NewEventDto event);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest event);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult requestStatusUpdate(Long userId, Long eventId, EventRequestStatusUpdateRequest requestStatus);

    List<EventFullDto> getAdminEvents(List<Long> users,
                                      List<EventState> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from,
                                      Integer size);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest event);

    List<EventShortDto> getPublicEvents(String text,
                                        List<Long> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        SortingOptions sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request);

    public EventFullDto getPublicEventById(Long id, HttpServletRequest request);

    public List<CommentFullDto> getPublicComments(Long id);
}

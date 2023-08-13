package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto requestCanceled(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequest(Long userId);
}

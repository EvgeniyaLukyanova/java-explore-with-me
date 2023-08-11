package ru.practicum.ewm.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.reference.EventState;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.reference.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    public RequestServiceImpl(RequestRepository repository, UserRepository userRepository, EventRepository eventRepository, RequestMapper requestMapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestMapper = requestMapper;
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IntegrityConstraintException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getInitiator().equals(user)) {
            throw new IntegrityConstraintException("Инициатор события не может добавлять запрос на участие в своём событии");
        }
        if (event.getParticipantLimit() != 0 && !event.getRequestModeration()) {
            if (repository.findByEventAndStatus(event, RequestStatus.CONFIRMED).size() >= event.getParticipantLimit()) {
                throw new IntegrityConstraintException("У события достигнут лимит запросов на участие");
            }
        }
        Request request = new Request();
        request.setRequester(user);
        request.setCreated(new Date());
        request.setEvent(event);
        request.setStatus(!event.getRequestModeration() || event.getParticipantLimit() == 0 ? RequestStatus.CONFIRMED : RequestStatus.PENDING);
        return requestMapper.requestToRequestDto(repository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto requestCanceled(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Request request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с ид %s не найден", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.requestToRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        return repository.findByRequester(user).stream()
                .map(requestMapper::requestToRequestDto)
                .collect(Collectors.toList());
    }
}

package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatisticClient;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.reference.AdminStateAction;
import ru.practicum.ewm.event.reference.EventState;
import ru.practicum.ewm.event.reference.SortingOptions;
import ru.practicum.ewm.event.reference.StateAction;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.event.storage.LocationRepository;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.pageable.FromSizePageable;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.reference.RequestStatus;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.constants.Constants.FORMATTER;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final StatisticClient statisticClient;

    public EventServiceImpl(EventRepository repository, LocationRepository locationRepository, UserRepository userRepository, CategoryRepository categoryRepository, EventMapper eventMapper, LocationMapper locationMapper, RequestRepository requestRepository, RequestMapper requestMapper, StatisticClient statisticClient) {
        this.repository = repository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.locationMapper = locationMapper;
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.statisticClient = statisticClient;
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категория с ид %s не найдена", eventDto.getCategory())));
        Location location = locationRepository.save(locationMapper.locationDtoToLocation(eventDto.getLocation()));
        Event event = eventMapper.newEventDtoToEvent(eventDto);
        if (event == null) {
            return null;
        } else {
            event.setCreatedOn(LocalDateTime.now());
            event.setState(EventState.PENDING);
            event.setCategory(category);
            event.setInitiator(user);
            event.setLocation(location);
        }
        return eventMapper.eventToEventFullDto(repository.save(event));
    }

    void update(Event event, UpdateEventRequest eventDto) {
        if (eventDto != null) {
            if (eventDto.getAnnotation() != null) {
                event.setAnnotation(eventDto.getAnnotation());
            }
            if (eventDto.getCategory() != null) {
                Category category = categoryRepository.findById(eventDto.getCategory())
                        .orElseThrow(() -> new NotFoundException(String.format("Категория с ид %s не найдена", eventDto.getCategory())));
                event.setCategory(category);
            }
            if (eventDto.getDescription() != null) {
                event.setDescription(eventDto.getDescription());
            }
            if (eventDto.getEventDate() != null) {
                event.setEventDate(eventDto.getEventDate());
            }
            if (eventDto.getLocation() != null) {
                if (!event.getLocation().getLon().equals(eventDto.getLocation().getLon()) ||
                        !event.getLocation().getLat().equals(eventDto.getLocation().getLat())) {
                    Location location = locationRepository.save(locationMapper.locationDtoToLocation(eventDto.getLocation()));
                    event.setLocation(location);
                }
            }
            if (eventDto.getPaid() != null) {
                event.setPaid(eventDto.getPaid());
            }
            if (eventDto.getParticipantLimit() != null) {
                event.setParticipantLimit(eventDto.getParticipantLimit());
            }
            if (eventDto.getRequestModeration() != null) {
                event.setRequestModeration(eventDto.getRequestModeration());
            }
            if (eventDto.getTitle() != null) {
                event.setTitle(eventDto.getTitle());
            }
        }
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        if (!List.of(EventState.CANCELED, EventState.PENDING).contains(event.getState())) {
            throw new IntegrityConstraintException("Изменитя события можно только отмененные или в состоянии ожидания модерации");
        }
        if (eventDto.getEventDate() != null) {
            if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new IntegrityConstraintException("дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
            }
        }
        update(event, eventDto);
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
            if (eventDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            }
        }
        return eventMapper.eventToEventFullDto(repository.save(event));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult requestStatusUpdate(Long userId, Long eventId, EventRequestStatusUpdateRequest requestStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        List<Request> requestList = requestRepository.findByIdIn(requestStatus.getRequestIds());
        Map<Long, Request> requestMap = new HashMap<>();
        for (Request r : requestList) {
            requestMap.put(r.getId(), r);
        }
        if (requestList.stream()
                .filter(e -> e.getStatus() != RequestStatus.PENDING)
                .collect(Collectors.toList()).size() > 0) {
            throw new IntegrityConstraintException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        if (requestStatus.getStatus().equals(RequestStatus.CONFIRMED)) {
            int count = requestRepository.findByEventAndStatus(event, RequestStatus.CONFIRMED).size();
            List<Request> confirmedRequests = new ArrayList<>();
            if (event.getParticipantLimit() != 0) {
                if (count >= event.getParticipantLimit()) {
                    throw new IntegrityConstraintException("У события достигнут лимит запросов на участие");
                }
                for (int i = 0; i < Math.min(event.getParticipantLimit() - count, requestStatus.getRequestIds().size()); i++) {
                    Request request = requestMap.get(requestStatus.getRequestIds().get(i));
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                }
            }
            List<Request> rejectedRequests = new ArrayList<>();
            for (Long l : requestStatus.getRequestIds()) {
                Request request = requestMap.get(l);
                if (!confirmedRequests.contains(request)) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(request);
                }
            }
            if (confirmedRequests.size() == 0) {
                result.setConfirmedRequests(new ArrayList<>());
            } else {
                result.setConfirmedRequests(confirmedRequests.stream()
                        .map(e -> requestMapper.requestToRequestDto(e))
                        .collect(Collectors.toList()));
            }
            if (rejectedRequests.size() == 0) {
                result.setRejectedRequests(new ArrayList<>());
            } else {
                result.setRejectedRequests(rejectedRequests.stream()
                        .map(e -> requestMapper.requestToRequestDto(e))
                        .collect(Collectors.toList()));
            }
        } else {
            result.setConfirmedRequests(new ArrayList<>());
            List<Request> rejectedRequests = new ArrayList<>();
            for (Long l : requestStatus.getRequestIds()) {
                Request request = requestMap.get(l);
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
            result.setRejectedRequests(rejectedRequests.stream()
                    .map(e -> requestMapper.requestToRequestDto(e))
                    .collect(Collectors.toList()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        FromSizePageable page = FromSizePageable.of(from, size, Sort.unsorted());
        List<Event> events = repository.findByInitiatorId(userId, page).toList();
        List<Request> requests = requestRepository.findByEventInAndStatus(events, RequestStatus.CONFIRMED);
        return events.stream()
                .map(e -> {
                    Integer confirmedRequests = requests.stream().filter(f -> f.getEvent().equals(e)).collect(Collectors.toList()).size();
                    EventShortDto eventShortDto = eventMapper.eventToEventShortDto(e);
                    eventShortDto.setConfirmedRequests(confirmedRequests.longValue());
                    return eventShortDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(String.format("Событие с ид %s добавлено не пользователем с ид %s", eventId, userId));
        }
        Integer confirmedRequests = requestRepository.findByEventAndStatus(event, RequestStatus.CONFIRMED).size();
        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setConfirmedRequests(confirmedRequests.longValue());
        return eventFullDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException(String.format("Событие с ид %s добавлено не пользователем с ид %s", eventId, userId));
        }
        return requestRepository.findRequestsEventsCurrentUser(userId, eventId).stream()
                .map(e -> requestMapper.requestToRequestDto(e))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        FromSizePageable page = FromSizePageable.of(from, size, Sort.unsorted());
        List<Event> events = repository.getEvents(users, states, categories, rangeStart, rangeEnd, page).toList();
        List<Request> requests = requestRepository.findByEventInAndStatus(events, RequestStatus.CONFIRMED);
        return events.stream()
                .map(e -> {
                    Integer confirmedRequests = requests.stream().filter(f -> f.getEvent().equals(e)).collect(Collectors.toList()).size();
                    EventFullDto eventFullDto = eventMapper.eventToEventFullDto(e);
                    eventFullDto.setConfirmedRequests(confirmedRequests.longValue());
                    return eventFullDto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest eventDto) {
        LocalDateTime publishedOn = LocalDateTime.now();
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) && !event.getState().equals(EventState.PENDING)) {
                throw new IntegrityConstraintException("Событие можно публиковать, только если оно в состоянии ожидания публикации");
            }
            if (eventDto.getStateAction().equals(AdminStateAction.REJECT_EVENT) && event.getState().equals(EventState.PUBLISHED)) {
                throw new IntegrityConstraintException("Событие можно отклонить, только если оно еще не опубликовано");
            }
        }
        if (eventDto.getEventDate() != null) {
            if (event.getPublishedOn() != null) {
                if (eventDto.getEventDate().isBefore(publishedOn.minusHours(1))) {
                    throw new IntegrityConstraintException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
                }
            }
        }
        update(event, eventDto);
        if (eventDto.getStateAction() != null) {
            if (eventDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(publishedOn);
            }
            if (eventDto.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                event.setState(EventState.CANCELED);
            }
        }
        return eventMapper.eventToEventFullDto(repository.save(event));
    }

    private void saveStat(HttpServletRequest request) {
        StatDto statDto = StatDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        statisticClient.createStatistic(statDto);
    }

    private Map<String, Long> getHit(String[] uris) {
        ResponseEntity<Object> response = statisticClient.getStatistics(LocalDateTime.now().minusYears(5),
                LocalDateTime.now().plusYears(5),
                uris,
                true);
        Object responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ViewStatDto> viewStatsDtos = objectMapper.convertValue(responseBody, new TypeReference<List<ViewStatDto>>() {});
        Map<String, Long> mapEventCount = new HashMap<>();
        for (ViewStatDto viewStatDto : viewStatsDtos) {
            mapEventCount.put(viewStatDto.getUri(), viewStatDto.getHits());
        }
        return mapEventCount;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortingOptions sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidationException(String.format("Некорретный период"));
            }
        }
        FromSizePageable page = FromSizePageable.of(from, size, Sort.unsorted());
        if (sort != null) {
            if (sort == SortingOptions.EVENT_DATE) {
                page = FromSizePageable.of(from, size, Sort.by("eventDate").ascending());
            }
        }
        List<Event> events = repository.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page).toList();
        List<Request> requests = requestRepository.findByEventInAndStatus(events, RequestStatus.CONFIRMED);
        Map<String, Long> mapEventCount = getHit(events.stream().map(e -> "/events/" + e.getId().toString()).toArray(String[]::new));
        List<EventShortDto> eventShortDtos = events.stream()
                .map(e -> {
                    Integer confirmedRequests = requests.stream().filter(f -> f.getEvent().equals(e)).collect(Collectors.toList()).size();
                    Long hits = mapEventCount.get("/events/" + e.getId().toString());
                    EventShortDto eventShortDto = eventMapper.eventToEventShortDto(e);
                    eventShortDto.setConfirmedRequests(confirmedRequests.longValue());
                    eventShortDto.setViews(hits == null ? 0 : hits);
                    return eventShortDto;
                }).collect(Collectors.toList());
        if (sort != null) {
            if (sort == SortingOptions.VIEWS) {
                return eventShortDtos.stream()
                        .sorted(Comparator.comparingLong(EventShortDto::getViews))
                        .collect(Collectors.toList());
            }
        }
        saveStat(request);
        return eventShortDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getPublicEventById(Long id, HttpServletRequest request) {
        Event event = repository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", id)));
        Integer confirmedRequests = requestRepository.findByEventAndStatus(event, RequestStatus.CONFIRMED).size();
        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setConfirmedRequests(confirmedRequests.longValue());
        Map<String, Long> mapEventCount = getHit(new String[]{"/events/" + event.getId().toString()});
        Long hits = mapEventCount.get("/events/" + event.getId().toString());
        eventFullDto.setViews(hits == null ? 0 : hits);
        saveStat(request);
        return eventFullDto;
    }
}

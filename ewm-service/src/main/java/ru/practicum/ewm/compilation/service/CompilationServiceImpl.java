package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationEventsMapper;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.CompilationEvents;
import ru.practicum.ewm.compilation.storage.CompilationEventsRepository;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pageable.FromSizePageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventsRepository compilationEventsRepository;
    private final CompilationMapper compilationMapper;
    private final CompilationEventsMapper compilationEventsMapper;
    private final EventRepository eventRepository;


    public CompilationServiceImpl(CompilationRepository compilationRepository,
                                  CompilationEventsRepository compilationEventsRepository,
                                  CompilationMapper compilationMapper,
                                  CompilationEventsMapper compilationEventsMapper,
                                  EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.compilationEventsRepository = compilationEventsRepository;
        this.compilationMapper = compilationMapper;
        this.compilationEventsMapper = compilationEventsMapper;
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        if (compilationMapper.compilationDtoToCompilation(compilationDto) == null) {
            return null;
        }
        Compilation compilation = compilationRepository.save(compilationMapper.compilationDtoToCompilation(compilationDto));
        List<Event> events = new ArrayList<>();
        if (compilationDto.getEvents() != null) {
            if (compilationDto.getEvents().size() != 0) {
                events = eventRepository.findByIdIn(compilationDto.getEvents());
                for (Event event : events) {
                    compilationEventsRepository.save(compilationEventsMapper.toCompilationEvents(event, compilation));
                }
            }
        }
        return compilationMapper.compilationToCompilationDto(compilation, events);
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с ид %s не найдена", compId)));
        List<CompilationEvents> compilationEvents = compilationEventsRepository.findByCompilation(compilation);
        if (compilationEvents.size() != 0) {
            compilationEventsRepository.deleteAllById(compilationEvents.stream().map(e -> e.getId()).collect(Collectors.toList()));
        }
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(UpdateCompilationRequest compilationDto, Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с ид %s не найдена", compId)));
        List<Event> events = new ArrayList<>();
        List<CompilationEvents> oldCompilationEvents = compilationEventsRepository.findByCompilation(compilation);
        List<Event> oldEvents = oldCompilationEvents.stream().map(e -> e.getEvent()).collect(Collectors.toList());
        if (compilationDto.getEvents() != null) {
            if (compilationDto.getEvents().size() != 0) {
                events = eventRepository.findByIdIn(compilationDto.getEvents());
                for (CompilationEvents compilationEvents : oldCompilationEvents) {
                    if (!events.contains(compilationEvents.getEvent())) {
                        compilationEventsRepository.deleteById(compilationEvents.getId());
                    }
                }
                for (Event event : events) {
                    if (!oldEvents.contains(event)) {
                        compilationEventsRepository.save(compilationEventsMapper.toCompilationEvents(event, compilation));
                    }
                }
            }
        } else {
            events = oldEvents;
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        return compilationMapper.compilationToCompilationDto(compilation, events);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilation(Boolean pinned, Integer from, Integer size) {
        FromSizePageable page = FromSizePageable.of(from, size, Sort.unsorted());
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, page).stream()
                    .map(e -> {List<CompilationEvents> compilationEvents = compilationEventsRepository.findByCompilation(e);
                        if (compilationEvents.size() != 0) {
                            List<Event> events = compilationEvents.stream().map(t -> t.getEvent()).collect(Collectors.toList());
                            return compilationMapper.compilationToCompilationDto(e, events);
                        } else {
                            return compilationMapper.compilationToCompilationDto(e, new ArrayList<>());
                        }})
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(page).stream()
                    .map(e -> {List<CompilationEvents> compilationEvents = compilationEventsRepository.findByCompilation(e);
                        if (compilationEvents.size() != 0) {
                            List<Event> events = compilationEvents.stream().map(t -> t.getEvent()).collect(Collectors.toList());
                            return compilationMapper.compilationToCompilationDto(e, events);
                        } else {
                            return compilationMapper.compilationToCompilationDto(e, new ArrayList<>());
                        }})
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с ид %s не найдена", compId)));
        List<CompilationEvents> compilationEvents = compilationEventsRepository.findByCompilation(compilation);
        if (compilationEvents.size() != 0) {
            List<Event> events = compilationEvents.stream().map(t -> t.getEvent()).collect(Collectors.toList());
            return compilationMapper.compilationToCompilationDto(compilation, events);
        } else {
            return compilationMapper.compilationToCompilationDto(compilation, new ArrayList<>());
        }
    }
}

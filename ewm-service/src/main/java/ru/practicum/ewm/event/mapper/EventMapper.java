package ru.practicum.ewm.event.mapper;

import org.mapstruct.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.Event;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", ignore = true)
    Event newEventDtoToEvent(NewEventDto eventDto);

    EventFullDto eventToEventFullDto(Event event);

    @Mapping(target = "category", ignore = true)
    Event eventToUpdateEventAdminRequest(UpdateEventAdminRequest eventDto);

    EventShortDto eventToEventShortDto(Event event);
}

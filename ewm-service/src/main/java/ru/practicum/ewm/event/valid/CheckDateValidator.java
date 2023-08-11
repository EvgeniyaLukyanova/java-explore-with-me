package ru.practicum.ewm.event.valid;

import ru.practicum.ewm.event.dto.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<EventDateValid, NewEventDto>  {
    @Override
    public void initialize(EventDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(NewEventDto eventDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime eventDate = eventDto.getEventDate();
        if (eventDate == null) {
            return false;
        }
        return eventDate.isAfter(LocalDateTime.now().plusHours(2)) || eventDate.equals(LocalDateTime.now().plusHours(2));
    }
}

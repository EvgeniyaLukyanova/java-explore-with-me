package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.reference.RequestStatus;
import java.util.Date;
import static ru.practicum.constants.Constants.DATE_FORMAT_Z;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_Z)
    private Date created;
    private Long event;
    private Long requester;
    private RequestStatus status;
}

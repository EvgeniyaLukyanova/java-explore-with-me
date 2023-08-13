package ru.practicum.ewm.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.reference.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEventAndStatus(Event event, RequestStatus status);

    List<Request> findByRequester(User user);

    List<Request> findByIdIn(List<Long> requestIds);

    @Query("select r " +
            "from Request as r " +
            "join r.event as e " +
            "join e.initiator as u " +
            "where u.id = :userId " +
            "  and e.id = :eventId")
    List<Request> findRequestsEventsCurrentUser(@Param("userId") Long userId, @Param("eventId") Long eventId);

    List<Request> findByEventInAndStatus(List<Event> events, RequestStatus status);
}

package ru.practicum.ewm.event.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.reference.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByIdIn(List<Long> ids);

    Page<Event> findByInitiatorId(Long userId, Pageable page);

    @Query("select e from Event as e " +
            "join e.initiator as u " +
            "join e.category as c " +
            "where (coalesce(:users, null) = null or u.id in (:users)) " +
            "  and (coalesce(:states, null) = null or e.state in (:states))" +
            "  and (coalesce(:categories, null) = null or c.id in (:categories)) " +
            "  and (coalesce(:rangeStart, null) = null or e.eventDate >= :rangeStart) " +
            "  and (coalesce(:rangeEnd, null) = null or e.eventDate <= :rangeEnd) "
    )
    Page<Event> getEvents(@Param("users") List<Long> users,
                          @Param("states") List<EventState> states,
                          @Param("categories") List<Long> categories,
                          @Param("rangeStart") LocalDateTime rangeStart,
                          @Param("rangeEnd") LocalDateTime rangeEnd,
                          @Param("page") Pageable page);

    @Query("select e from Event as e " +
            "join e.category as c " +
            "where e.state = ru.practicum.ewm.event.reference.EventState.PUBLISHED " +
            "  and (Lower(e.annotation) like '%'||Lower(:text)||'%' or Lower(e.description) like '%'||Lower(:text)||'%' or :text = null) " +
            "  and (coalesce(:categories, null) = null or c.id in (:categories)) " +
            "  and (coalesce(:rangeStart, null) = null or e.eventDate >= :rangeStart) " +
            "  and (coalesce(:rangeEnd, null) = null or e.eventDate <= :rangeEnd) " +
            "  and ((coalesce(:rangeStart, null) = null and coalesce(:rangeEnd, null) = null and e.eventDate >= CURRENT_TIMESTAMP) " +
            "       or coalesce(:rangeStart, null) <> null or coalesce(:rangeEnd, null) <> null) " +
            "  and (:paid = null or e.paid = :paid) " +
            "  and ((:onlyAvailable = true and e.participantLimit > (select count(r) from Request as r " +
            "                                                        where r.status = 'CONFIRMED' " +
            "                                                          and r.event = e)) " +
            "       or (:onlyAvailable = false)) "
    )
    Page<Event> getPublicEvents(@Param("text") String text,
                                @Param("categories") List<Long> categories,
                                @Param("paid") Boolean paid,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                @Param("onlyAvailable") Boolean onlyAvailable,
                                @Param("page") Pageable page);

    Optional<Event> findByIdAndState(Long id, EventState state);
}

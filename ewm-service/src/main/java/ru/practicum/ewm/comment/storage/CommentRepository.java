package ru.practicum.ewm.comment.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEvent(Event event);

    List<Comment> findByEventIn(List<Event> events);

    @Query("select c from Comment as c " +
            "join c.author as u " +
            "join c.event as e " +
            "where (coalesce(:users, null) = null or u.id in (:users)) " +
            "  and (coalesce(:events, null) = null or e.id in (:events)) " +
            "  and (coalesce(:rangeStart, null) = null or c.created >= :rangeStart) " +
            "  and (coalesce(:rangeEnd, null) = null or c.created <= :rangeEnd) "
    )
    Page<Comment> getComments(@Param("users") List<Long> users,
                              @Param("events") List<Long> events,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              @Param("rangeEnd") LocalDateTime rangeEnd,
                              @Param("page") Pageable page);
}

package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, CommentDto commentDto);

    void delete(Long userId, Long commentId);

    CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto);

    CommentFullDto getCommentById(Long commentId);

    List<CommentFullDto> getAdminComments(List<Long> users,
                                          List<Long> events,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Integer from,
                                          Integer size);

    void adminDelete(Long commentId);
}

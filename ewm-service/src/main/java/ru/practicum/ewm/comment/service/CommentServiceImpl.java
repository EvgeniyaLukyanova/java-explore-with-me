package ru.practicum.ewm.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.storage.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.reference.EventState;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.IntegrityConstraintException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pageable.FromSizePageable;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public CommentDto createComment(Long userId, Long eventId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ид %s не найдено", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IntegrityConstraintException("Нельзя добавить комментарии к неопубликованному событию");
        }
        Comment comment = commentMapper.commentDtoToComment(commentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setEvent(event);
        return commentMapper.commentToCommentDto(repository.save(comment));
    }

    @Transactional
    @Override
    public void delete(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Коментарий с ид %s не найден", commentId)));
        if (!comment.getAuthor().equals(user)) {
            throw new IntegrityConstraintException("Коментарий может удалить только его автор");
        }
        repository.deleteById(commentId);
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользовать с ид %s не найден", userId)));
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Коментарий с ид %s не найден", commentId)));
        if (!comment.getAuthor().equals(user)) {
            throw new IntegrityConstraintException("Коментарий может редактировать только его автор");
        }
        comment.setText(commentDto.getText());
        return commentMapper.commentToCommentDto(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public CommentFullDto getCommentById(Long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Коментарий с ид %s не найден", commentId)));
        return commentMapper.commentToCommentFullDto(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentFullDto> getAdminComments(List<Long> users,
                                                 List<Long> events,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size) {
        FromSizePageable page = FromSizePageable.of(from, size, Sort.unsorted());
        List<Comment> comments = repository.getComments(users, events, rangeStart, rangeEnd, page).toList();
        return comments.stream().map(e -> commentMapper.commentToCommentFullDto(e)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void adminDelete(Long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Коментарий с ид %s не найден", commentId)));
        repository.deleteById(commentId);
    }
}

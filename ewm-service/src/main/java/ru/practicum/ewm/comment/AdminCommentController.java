package ru.practicum.ewm.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.service.CommentService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constants.Constants.DATE_FORMAT;

@RestController
@RequestMapping(path = "/admin/comments")
@Slf4j
@AllArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentFullDto> getAdminComments(@RequestParam(name = "users", required = false) List<Long> users,
                                                       @RequestParam(name = "events", required = false) List<Long> events,
                                                       @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                                       @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получение списка комментариев администратором");
        return commentService.getAdminComments(users, events, rangeStart, rangeEnd, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        log.info("Удаление комментария администратором с ид {}", commentId);
        commentService.adminDelete(commentId);
    }
}

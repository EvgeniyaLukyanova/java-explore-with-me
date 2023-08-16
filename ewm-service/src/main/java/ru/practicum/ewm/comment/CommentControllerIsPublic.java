package ru.practicum.ewm.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentFullDto;
import ru.practicum.ewm.comment.service.CommentService;

@RestController
@RequestMapping(path = "/comments")
@Slf4j
@AllArgsConstructor
public class CommentControllerIsPublic {
    private final CommentService commentService;

    @GetMapping("/{commitId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto getCommentById(@PathVariable Long commitId) {
        log.info("Получение коментария по ид {}", commitId);
        return commentService.getCommentById(commitId);
    }
}

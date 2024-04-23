package com.example.stockfeed.Controller;

import com.example.stockfeed.Dto.CommentDto;
import com.example.stockfeed.Dto.CreateCommentDto;
import com.example.stockfeed.Service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post/comment") // 기본 경로
public class CommentController {
    private final CommentService commentService;


    /**
     * 댓글 작성
     */
    @PostMapping("/create")
    public ResponseEntity<String> createComment(@RequestBody CreateCommentDto createCommentDto) {
        commentService.createComment(createCommentDto.getPostId(), createCommentDto.getContent());
        return ResponseEntity.ok().body("댓글 작성 완료.");
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteComment(@RequestBody CommentDto commentId) {
        commentService.deleteComment(commentId.getId());
        return ResponseEntity.ok().body("댓글 삭제 완료.");
    }

    /**
     * 댓글 좋아요
     */
    @PostMapping("/like")
    public ResponseEntity<String> likeComment(@RequestBody CommentDto commentId) {
        commentService.likeComment(commentId.getId());
        return ResponseEntity.ok().body("댓글 좋아요 완료.");
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

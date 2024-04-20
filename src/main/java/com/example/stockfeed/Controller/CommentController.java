package com.example.stockfeed.Controller;

import com.example.stockfeed.Service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void createComment(Long postId, String content) {
        commentService.createComment(postId, content);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/delete")
    public void deleteComment(Long commentId) {
        commentService.deleteComment(commentId);
    }

    /**
     * 댓글 좋아요
     */
    @PostMapping("/like")
    public void likeComment(Long commentId) {
        commentService.likeComment(commentId);
    }
}

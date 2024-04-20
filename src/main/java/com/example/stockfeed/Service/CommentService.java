package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.Comment;
import com.example.stockfeed.Domain.CommentLike;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Dto.CommentDto;
import com.example.stockfeed.Repository.CommentLikeRepository;
import com.example.stockfeed.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final CommentLikeRepository commentLikeRepository;

    // 댓글 생성
    public void createComment(Long postId, String content) {
        String email = userService.getCurrentUser();
        User user = userService.getUser(email);

        Comment comment = Comment.builder()
                .user(user)
                .content(content)
                .post(postService.getPostById(postId))
                .build();
        commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        String user = userService.getCurrentUser();
        if(!comment.getUser().getEmail().equals(user)){
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    // 게시글의 댓글 리스트 조회
    public List<CommentDto> getComments(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAt(postId);
        return comments.stream().map(comment -> CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .likecount(comment.getLikeCount())
                .writer(comment.getUser().getUsername())
                .createdAt(comment.getCreatedAt())
                .isLiked(commentLikeRepository.existsByUserAndComment(userService.getUser(userService.getCurrentUser()), comment))
                .build()).toList();
    }

    // 댓글 좋아요
    public void likeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        User user = userService.getUser(userService.getCurrentUser());
        if(commentLikeRepository.existsByUserAndComment(user, comment)){
            throw new IllegalArgumentException("이미 좋아요를 누른 댓글입니다.");
        }
        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
        commentLikeRepository.save(commentLike);
    }

}

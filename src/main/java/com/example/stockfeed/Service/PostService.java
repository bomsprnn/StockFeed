package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.Comment;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.PostLike;
import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Dto.CommentDto;
import com.example.stockfeed.Dto.CreatePostDto;
import com.example.stockfeed.Dto.ViewPostDto;
import com.example.stockfeed.Repository.CommentLikeRepository;
import com.example.stockfeed.Repository.CommentRepository;
import com.example.stockfeed.Repository.PostLikeRepository;
import com.example.stockfeed.Repository.PostRepository;
import com.example.stockfeed.Service.NewsFeedEvent.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

     // 게시글 생성
    public void createPost(CreatePostDto createPostDto) {
        User user = userService.getCurrentUserEntity();
        Post post = Post.builder()
                .title(createPostDto.getTitle())
                .content(createPostDto.getContent())
                .user(user)
                .viewCount(0)
                .build();
        postRepository.save(post);
       applicationEventPublisher.publishEvent(new PostEvent(this, post, PostEvent.EventType.CREATE));
    }

    // 게시글 수정
    public void updatePost(Long postId, CreatePostDto createPostDto) {
        Post post = checkAndGetPost(postId);
        post.update(createPostDto.getTitle(), createPostDto.getContent());
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = checkAndGetPost(postId);
        postRepository.delete(post);
    }

    // 게시글 조회및 권한 체크
    private Post checkAndGetPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        if (!post.getUser().getUsername().equals(userService.getCurrentUser())) {
            throw new IllegalArgumentException("게시글 작성자에게만 권한이 있습니다.");
        }
        return post;
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }

    // 게시글 상세 조회
    public ViewPostDto viewPost(Long postId) {
        Post post = getPostById(postId);
        post.addViewCount(); // 조회수 증가
        User user = userService.getCurrentUserEntity();
        List<CommentDto> commentDtos = fetchCommentsForPost(post, user);
        int like = postLikeRepository.countByPost(post);
        boolean isLiked = postLikeRepository.existsByUserAndPost(userService.getUser(userService.getCurrentUser()), post);
        return ViewPostDto.builder()
                .comments(commentDtos)
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getUsername())
                .date(post.getCreatedAt().toString())
                .viewCount(post.getViewCount())
                .likeCount(like)
                .isLiked(isLiked)
                .build();
    }

    // 포스트 좋아요
    public void likePost(Long postId) {
        Post post = getPostById(postId);
        User user = userService.getCurrentUserEntity();

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }
        PostLike postLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();
        postLikeRepository.save(postLike);
    }

    // 포스트 좋아요 취소
    public void unlikePost(Long postId) {
        Post post = getPostById(postId);
        User user = userService.getCurrentUserEntity();

        if (!postLikeRepository.existsByUserAndPost(user, post)) {
            throw new IllegalArgumentException("좋아요를 누르지 않은 게시글입니다.");
        }
        postLikeRepository.deleteByUserAndPost(user, post);
    }

    // 포스트 댓글 가져오기
    private List<CommentDto> fetchCommentsForPost(Post post, User user) {
        return post.getComment().stream().map(comment -> {
            boolean isLiked = commentLikeRepository.existsByUserAndComment(user, comment);
            int likeCount = commentLikeRepository.countByComment(comment);
            return new CommentDto(
                    comment.getId(),
                    comment.getContent(),
                    comment.getUser().getUsername(),
                    comment.getCreatedAt(),
                    likeCount,
                    isLiked
            );
        }).collect(Collectors.toList());
    }


}

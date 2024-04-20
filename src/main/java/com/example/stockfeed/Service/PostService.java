package com.example.stockfeed.Service;

import com.example.stockfeed.Domain.Comment;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.PostLike;
import com.example.stockfeed.Domain.User;
import com.example.stockfeed.Dto.CommentDto;
import com.example.stockfeed.Dto.CreatePostDto;
import com.example.stockfeed.Dto.ViewPostDto;
import com.example.stockfeed.Repository.PostLikeRepository;
import com.example.stockfeed.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final PostLikeRepository postLikeRepository;

    // 게시글 생성
    public void createPost(CreatePostDto createPostDto) {
        String username = userService.getCurrentUser();
        User user = userService.getUser(username);
        Post post = Post.builder()
                .title(createPostDto.getTitle())
                .content(createPostDto.getContent())
                .user(user)
                .viewCount(0)
                .build();
        postRepository.save(post);
    }

    // 게시글 수정 폼 빌드 위해 조회
    public ViewPostDto getPostForUpdate(Long postId) {
        Post post = checkAndGetPost(postId);
        ViewPostDto viewPostDto = ViewPostDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getUsername())
                .date(post.getCreatedAt().toString())
                .viewCount(post.getViewCount())
                .build();
        return viewPostDto;
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
        if (post.getUser().getUsername().equals(userService.getCurrentUser())) {
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
        post.addViewCount();
        List<CommentDto> comments = commentService.getComments(postId);
        int like = postLikeRepository.countByPost(post);
        boolean isLiked = postLikeRepository.existsByUserAndPost(userService.getUser(userService.getCurrentUser()), post);
        return ViewPostDto.builder()
                .comments(comments)
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
        User user = userService.getUser(userService.getCurrentUser());

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }
        PostLike postLike = PostLike.builder()
                .user(user)
                .post(post)
                .build();
        postLikeRepository.save(postLike);
    }


}

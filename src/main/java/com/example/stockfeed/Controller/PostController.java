package com.example.stockfeed.Controller;

import com.example.stockfeed.Dto.CreatePostDto;
import com.example.stockfeed.Service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post") // 기본 경로
public class PostController {
    private final PostService postService;


    /**
     * 포스트 작성
     */
    @PostMapping("/create")
    public void createPost(@RequestBody CreatePostDto createPostDto) {
        postService.createPost(createPostDto);
    }

    /**
     * 포스트 조회
     */
    @GetMapping("/{postId}")
    public void viewPost(@PathVariable Long postId) {
        postService.viewPost(postId);
    }

    /**
     * 포스트 삭제
     */
    @DeleteMapping("/delete/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
    }

    /**
     * 포스트 수정
     */
    // 수정 폼 빌드 위해 조회
    @GetMapping("/update/{postId}")
    public void getPostForUpdate(@PathVariable Long postId) {
        postService.getPostForUpdate(postId);
    }

    // 수정
    @PutMapping("/update/{postId}")
    public void updatePost(@PathVariable Long postId, @RequestBody CreatePostDto createPostDto) {
        postService.updatePost(postId, createPostDto);
    }

    /**
     * 포스트 좋아요
     */
    @PostMapping("/like/{postId}")
    public void likePost(@PathVariable Long postId) {
        postService.likePost(postId);
    }

}

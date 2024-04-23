package com.example.stockfeed;

import com.example.stockfeed.Dto.CreatePostDto;
import com.example.stockfeed.Repository.PostRepository;
import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Service.PostService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @AfterEach
    public void cleanup() {
        postRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createPost_savesPostSuccessfully() {
        // Given
        String title = "테스트777";
        String content = "테스트=ㅠㅜ";
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setTitle(title);
        createPostDto.setContent(content);

        // When
        postService.createPost(createPostDto);

        // Then
        Post savedPost = postRepository.findAll().get(0);
        Assertions.assertEquals(title, savedPost.getTitle());
        Assertions.assertEquals(content, savedPost.getContent());
    }
}

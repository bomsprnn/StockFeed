package com.example.stockfeed.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createdAt;
    private int likecount;
    private boolean isLiked;

    @Builder
    public CommentDto(Long id, String content, String writer, LocalDateTime createdAt, int likecount, boolean isLiked) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.createdAt = createdAt;
        this.likecount = likecount;
        this.isLiked = isLiked;
    }


}



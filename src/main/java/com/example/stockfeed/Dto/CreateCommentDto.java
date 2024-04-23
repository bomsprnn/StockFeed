package com.example.stockfeed.Dto;

import lombok.Data;

@Data
public class CreateCommentDto {
    private Long postId;
    private String content;
}

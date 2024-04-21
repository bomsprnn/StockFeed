package com.example.stockfeed.Dto;

import com.example.stockfeed.Domain.Comment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ViewPostDto {
    private String title;
    private String content;
    private String author;
    private String date;
    private int viewCount;
    private List<CommentDto> comments;
    private int likeCount;
    private boolean isLiked;



    @Builder
    public ViewPostDto(String title, String content, String author, String date, int viewCount, List<CommentDto> comments, int likeCount, boolean isLiked) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.date = date;
        this.viewCount = viewCount;
        this.comments = comments;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
    }

}

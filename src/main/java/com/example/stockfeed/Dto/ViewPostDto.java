package com.example.stockfeed.Dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ViewPostDto {
    private String title;
    private String content;
    private String author;
    private String date;
    private int viewCount;

    @Builder
    public ViewPostDto(String title, String content, String author, String date, int viewCount){
        this.title=title;
        this.content=content;
        this.author=author;
        this.date=date;
        this.viewCount=viewCount;
    }
}

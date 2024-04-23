package com.example.stockfeed.Service.NewsFeedEvent;

import com.example.stockfeed.Domain.CommentLike;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentLikeEvent extends ApplicationEvent {
    private final CommentLike commentLike;

    public CommentLikeEvent(Object source, CommentLike commentLike) {
        super(source);
        this.commentLike = commentLike;
    }



}

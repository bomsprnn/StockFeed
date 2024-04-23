package com.example.stockfeed.Service.NewsFeedEvent;

import com.example.stockfeed.Domain.Post;
import com.example.stockfeed.Domain.PostLike;
import com.example.stockfeed.Domain.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostLikeEvent extends ApplicationEvent {
    private final PostLike postLike;
    private final Post post;
    private final User user;
    private final EventType eventType;

    public enum EventType {
        CREATE,
        DELETE
    }

    public PostLikeEvent(Object source, PostLike postLike, Post post, User user, EventType eventType) {
        super(source);
        this.postLike = postLike;
        this.post = post;
        this.user = user;
        this.eventType = eventType;
    }
}

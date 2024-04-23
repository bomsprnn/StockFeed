package com.example.stockfeed.Service.NewsFeedEvent;

import com.example.stockfeed.Domain.Post;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PostEvent extends ApplicationEvent {
    private final Post post;
    private final EventType eventType;
    public enum EventType {
        CREATE,
        DELETE
    }
    public PostEvent(Object source, Post post, EventType eventType) {
        super(source);
        this.post = post;
        this.eventType = eventType;
    }
}

package com.example.stockfeed.Service.NewsFeedEvent;

import com.example.stockfeed.Domain.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentEvent extends ApplicationEvent {
    private Comment comment;
    private EventType eventType;
    public enum EventType {
        CREATE,
        DELETE
    }
    public CommentEvent(Object source, Comment comment, EventType eventType) {
        super(source);
        this.comment = comment;
        this.eventType = eventType;
    }
}

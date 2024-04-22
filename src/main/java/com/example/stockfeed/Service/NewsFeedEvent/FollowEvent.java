package com.example.stockfeed.Service.NewsFeedEvent;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FollowEvent extends ApplicationEvent {
    private Long followerId;
    private Long followeeId;
    private EventType eventType;

    public enum EventType {
        FOLLOW,
        UNFOLLOW
    }

    public FollowEvent(Object source, Long followerId, Long followeeId, EventType eventType) {
        super(source);
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.eventType = eventType;
    }
}

package com.example.stockfeed.Service.NewsFeedEvent;

import com.example.stockfeed.Domain.Follow;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FollowEvent extends ApplicationEvent {
    private Follow follow;
    private Long followerId;
    private Long followeeId;
    private EventType eventType;

    public enum EventType {
        FOLLOW,
        UNFOLLOW
    }

    public FollowEvent(Object source, Follow follow, Long followerId, Long followeeId, EventType eventType) {
        super(source);
        this.follow = follow;
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.eventType = eventType;
    }
}

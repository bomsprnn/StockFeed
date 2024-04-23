package com.example.stockfeed.Dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class NewsFeedDto {
    private Long id;
    private Long userId;
    private Long ownUserId;
    private String type;
    private Long followUserId;
    private Long postId;
    private Long commentId;

    public NewsFeedDto(Long id, Long userId, Long ownUserId, String type, Long followUserId, Long postId, Long commentId) {
        this.id = id;
        this.userId = userId;
        this.ownUserId = ownUserId;
        this.type = type;
        this.followUserId = followUserId;
        this.postId = postId;
        this.commentId = commentId;
    }

}

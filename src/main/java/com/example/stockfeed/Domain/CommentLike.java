package com.example.stockfeed.Domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class CommentLike extends BaseEntity{ //댓글 좋아요
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //유저와 연관관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; //댓글과 연관관계

    @Builder
    public CommentLike(User user, Comment comment){
        this.user = user;
        this.comment = comment;
    }


}

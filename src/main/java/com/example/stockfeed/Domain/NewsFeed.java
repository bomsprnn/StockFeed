package com.example.stockfeed.Domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class NewsFeed extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "newsfeed_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; //뉴스피드 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "own_user_id")
    private User ownUser;
    //컨텐츠 게시자 (게시글, 댓글 작성자), 행위자
    @Enumerated(EnumType.STRING)
    private NewsFeedType type; //뉴스피드 타입

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follow_user_id")
    private User followUser; //팔로우한 유저


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post; //게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment; //댓글

    @Builder
    public NewsFeed(User user, User ownUser, NewsFeedType type, Post post, Comment comment, User followUser) {
        this.user = user;
        this.ownUser = ownUser;
        this.type = type;
        this.post = post;
        this.comment = comment;
        this.followUser = followUser;
    }



}

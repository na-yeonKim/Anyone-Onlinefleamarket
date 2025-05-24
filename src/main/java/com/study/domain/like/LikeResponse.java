package com.study.domain.like;

import com.study.domain.post.PostResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LikeResponse {
    private Long id;
    private Long memberId;
    private Long postId;

    // 게시글 정보도 포함시킴
    private String title;
    private String writer;
    private int price;
    private LocalDateTime createdDate;
}
package com.study.domain.post;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private Long id;                       // PK
    private String title;                  // 제목
    private String content;                // 내용
    private String writer;                 // 작성자
    private Boolean sellYn;                // 공지글 여부
    private Boolean deleteYn;              // 삭제 여부
    private LocalDateTime createdDate;     // 생성일시
    private LocalDateTime modifiedDate;    // 최종 수정일시
    private int price;                     // 가격
    private Boolean isAutoPrice;           // 자동 가격 산정 여부
    private String productName;            // 상품명
    private String usagePeriod;            // 사용 기간
    private Boolean isOpened;              // 개봉 여부
    private String condition;              // 상품 상태

}
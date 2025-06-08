package com.study.domain.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostRequest {

    private Long id;                                        // 게시글 ID
    private String title;                                   // 게시글 제목
    private String content;                                 // 게시글 내용
    private String writer;                                  // 작성자명
    private Boolean sellYn;                                 // 판매 여부
    private Integer deleteYn;                               // 삭제 여부
    private Integer price;                                  // 판매 가격
    private Boolean isAutoPrice;                            // 자동 가격 산정 여부
    private String productName;                             // 상품명
    private String usagePeriod;                             // 사용 기간
    private Boolean isOpened;                               // 개봉 여부
    private String condition;                               // 상품 상태

    private List<MultipartFile> files = new ArrayList<>();  // 첨부 파일 목록
    private List<Long> removeFileIds = new ArrayList<>();   // 삭제할 파일 ID 목록
}
package com.study.domain.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostRequest {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private Boolean sellYn;
    private Integer deleteYn;
    private Integer price;
    private Boolean isAutoPrice;
    private String productName;
    private String usagePeriod;
    private Boolean isOpened;
    private String condition;

    private List<MultipartFile> files = new ArrayList<>();
    private List<Long> removeFileIds = new ArrayList<>();
}
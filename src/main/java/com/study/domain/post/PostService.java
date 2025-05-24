package com.study.domain.post;

import com.study.common.dto.SearchDto;
import com.study.common.paging.Pagination;
import com.study.common.paging.PagingResponse;
import com.study.domain.price.PriceScraper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PriceScraper priceScraper;

    /**
     * 게시글 저장
     * @param params - 게시글 정보
     * @return Generated PK
     */
    @Transactional
    public Long savePost(final PostRequest params) {
        if (params.getDeleteYn() == null) {
            params.setDeleteYn(0);
        }
        if (Boolean.TRUE.equals(params.getIsAutoPrice())) {
            try {
                int finalPrice = calculateAutoPrice(
                        params.getProductName(),
                        params.getUsagePeriod(),
                        params.getIsOpened().toString(),
                        params.getCondition()
                );
                params.setPrice(finalPrice);
            } catch (Exception e) {
                params.setIsAutoPrice(false);
            }
        }
        postMapper.save(params);
        return params.getId();
    }

    /**
     * 게시글 상세정보 조회
     * @param id - PK
     * @return 게시글 상세정보
     */
    public PostResponse findPostById(final Long id) {
        return postMapper.findById(id);
    }

    /**
     * 게시글 수정
     * @param params - 게시글 정보
     * @return PK
     */
    @Transactional
    public Long updatePost(final PostRequest params) {
        // 오류 방지
        if (params.getPrice() == null) {
            params.setPrice(0);
        }

        postMapper.update(params);
        return params.getId();
    }

    /**
     * 게시글 삭제
     * @param id - PK
     * @return PK
     */
    public Long deletePost(final Long id) {
        postMapper.deleteById(id);
        return id;
    }

    /**
     * 게시글 리스트 조회
     * @param params - search conditions
     * @return list & pagination information
     */
    public PagingResponse<PostResponse> findAllPost(final SearchDto params) {

        // 조건에 해당하는 데이터가 없는 경우, 응답 데이터에 비어있는 리스트와 null을 담아 반환
        int count = postMapper.count(params);
        if (count < 1) {
            return new PagingResponse<>(Collections.emptyList(), null);
        }

        // Pagination 객체를 생성해서 페이지 정보 계산 후 SearchDto 타입의 객체인 params에 계산된 페이지 정보 저장
        Pagination pagination = new Pagination(count, params);
        params.setPagination(pagination);

        // 계산된 페이지 정보의 일부(limitStart, recordSize)를 기준으로 리스트 데이터 조회 후 응답 데이터 반환
        List<PostResponse> list = postMapper.findAll(params);
        return new PagingResponse<>(list, pagination);
    }

    public int calculateAutoPrice(String productName, String usagePeriod, String isOpened, String condition) {
        int basePrice = priceScraper.getAveragePrice(productName);

        double usageFactor = Double.parseDouble(usagePeriod);
        double conditionFactor = Double.parseDouble(condition);
        double openedFactor = Boolean.parseBoolean(isOpened) ? 0.9 : 1.0;

        return (int) (basePrice * usageFactor * conditionFactor * openedFactor);
    }

}
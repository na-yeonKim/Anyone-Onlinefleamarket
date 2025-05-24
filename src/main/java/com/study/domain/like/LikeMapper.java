package com.study.domain.like;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LikeMapper {

    // 찜 여부 존재 확인
    boolean exists(LikeRequest request);

    // 찜 저장
    void save(LikeRequest request);

    // 찜 삭제
    void delete(LikeRequest request);

    // 사용자가 찜한 게시글 목록 조회
    List<LikeResponse> findAllByMemberId(Long memberId);
}
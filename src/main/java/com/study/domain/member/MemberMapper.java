package com.study.domain.member;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {

    /**
     * 회원 정보 저장 (회원가입)
     * @param params - 회원 정보
     */
    void save(MemberRequest params);

    /**
     * 회원 상세정보 조회
     * @param loginId - UK
     * @return 회원 상세정보
     */
    MemberResponse findByLoginId(String loginId);

    /**
     * 회원 정보 수정
     * @param params - 회원 정보
     */
    void update(MemberRequest params);

    /**
     * 회원 정보 삭제 (회원 탈퇴)
     * @param id - PK
     */
    void deleteById(Long id);

    /**
     * 회원 수 카운팅 (ID 중복 체크)
     * @param loginId - UK
     * @return 회원 수
     */
    int countByLoginId(String loginId);

    /**
     * 회원 신고 횟수 증가
     * @param id - 회원 고유 번호 (PK)
     */
    void updateReportCount(Long id);

    /**
     * 신고된 회원 목록 조회 (report_count > 0)
     * @return 신고된 회원 목록
     */
    List<MemberResponse> findReportedMembers();

    /**
     * 회원 고유 번호로 회원 상세 정보 조회
     * @param id - 회원 고유 번호 (PK)
     * @return 회원 상세 정보 DTO
     */
    MemberResponse findById(Long id);

}
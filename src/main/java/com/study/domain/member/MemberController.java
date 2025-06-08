package com.study.domain.member;

import com.study.domain.like.LikeResponse;
import com.study.domain.like.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final LikeService likeService;

    // 로그인 페이지
    @GetMapping("/login.do")
    public String openLogin() {
        return "member/login";
    }

    // 회원 정보 저장 (회원가입)
    @PostMapping("/members")
    @ResponseBody
    public Long saveMember(@RequestBody final MemberRequest params) {
        return memberService.saveMember(params);
    }

    // 회원 상세정보 조회
    @GetMapping("/members/{loginId}")
    @ResponseBody
    public MemberResponse findMemberByLoginId(@PathVariable final String loginId) {
        return memberService.findMemberByLoginId(loginId);
    }

    // 회원 정보 수정
    @PatchMapping("/members/{id}")
    @ResponseBody
    public Long updateMember(@PathVariable final Long id, @RequestBody final MemberRequest params) {
        return memberService.updateMember(params);
    }

    // 회원 정보 삭제 (회원 탈퇴)
    @DeleteMapping("/members/{id}")
    @ResponseBody
    public Long deleteMemberById(@PathVariable final Long id) {
        return memberService.deleteMemberById(id);
    }

    // 회원 수 카운팅 (ID 중복 체크)
    @GetMapping("/member-count")
    @ResponseBody
    public int countMemberByLoginId(@RequestParam final String loginId) {
        return memberService.countMemberByLoginId(loginId);
    }

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public MemberResponse login(HttpServletRequest request) {

        // 1. 회원 정보 조회
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        MemberResponse member = memberService.login(loginId, password);

        // 2. 세션에 회원 정보 저장 & 세션 유지 시간 설정
        if (member != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loginMember", member);
            session.setMaxInactiveInterval(60 * 30);
        }

        return member;
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login.do";
    }

    // 회원 신고 처리
    @PostMapping("/members/report/{loginId}")
    @ResponseBody
    public ResponseEntity<?> reportMember(@PathVariable String loginId) {
        MemberResponse member = memberService.findMemberByLoginId(loginId);
        if (member == null) {
            return ResponseEntity.badRequest().body("해당 회원을 찾을 수 없습니다.");
        }
        memberService.reportMember(member.getId());
        return ResponseEntity.ok("신고가 완료되었습니다.");
    }

    // 신고된 회원 목록 조회 (관리자 전용 페이지)
    @GetMapping("/admin/reported-members")
    public String reportedMembers(Model model, HttpSession session) {
        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        if (loginMember == null || !"ADMIN".equals(loginMember.getRole())) {
            return "redirect:/login.do"; // 또는 오류 페이지
        }

        List<MemberResponse> reportedList = memberService.getReportedMembers();
        model.addAttribute("reportedMembers", reportedList);
        return "admin/reported-members";
    }

    // 관리자에 의한 회원 강제 탈퇴 처리
    @PostMapping("/admin/members/{id}/delete")
    public String forceDeleteMember(@PathVariable Long id, HttpSession session) {
        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        if (loginMember == null || !"ADMIN".equals(loginMember.getRole())) {
            return "redirect:/login.do";
        }

        memberService.deleteMemberById(id);
        return "redirect:/admin/reported-members";
    }

    // 회원 상세 정보 조회 페이지 (회원 + 찜한 게시글 목록 포함)
    @GetMapping("/members/detail/{id}")
    public String memberDetail(@PathVariable Long id, Model model) {
        MemberResponse member = memberService.findMemberById(id);
        List<LikeResponse> likedPosts = likeService.findLikesByMemberId(id);

        model.addAttribute("member", member);
        model.addAttribute("likedPosts", likedPosts);

        return "member/detail"; // member/detail.html
    }

}
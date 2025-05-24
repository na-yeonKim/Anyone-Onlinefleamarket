package com.study.domain.like;

import com.study.domain.member.MemberResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public void toggleLike(@PathVariable Long postId, HttpSession session) {
        var loginMember = (MemberResponse) session.getAttribute("loginMember");
        LikeRequest request = new LikeRequest();
        request.setMemberId(loginMember.getId());
        request.setPostId(postId);
        likeService.toggleLike(request);
    }

    @GetMapping("/members/{memberId}/likes")
    public List<LikeResponse> getLikedPosts(@PathVariable Long memberId) {
        return likeService.findLikesByMemberId(memberId);
    }
}
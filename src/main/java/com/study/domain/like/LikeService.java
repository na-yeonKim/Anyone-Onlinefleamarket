package com.study.domain.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeMapper likeMapper;

    @Transactional
    public void toggleLike(LikeRequest request) {
        if (likeMapper.exists(request)) {
            likeMapper.delete(request);
        } else {
            likeMapper.save(request);
        }
    }

    public List<LikeResponse> findLikesByMemberId(Long memberId) {
        return likeMapper.findAllByMemberId(memberId);
    }

}
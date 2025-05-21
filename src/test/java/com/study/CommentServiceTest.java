package com.study;

import com.study.domain.comment.CommentRequest;
import com.study.domain.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Test
    void saveCommentByForeach() {
        for (int i = 1; i <= 100; i++) {
            CommentRequest params = new CommentRequest();
            params.setPostId(1L); // 게시글 1번에 댓글 작성
            params.setContent(i + "번째 댓글 내용입니다.");
            params.setWriter("댓글작성자" + i);

            commentService.saveComment(params);
        }
    }
}

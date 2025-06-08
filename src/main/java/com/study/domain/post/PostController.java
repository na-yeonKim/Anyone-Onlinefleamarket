package com.study.domain.post;

import com.study.common.dto.MessageDto;
import com.study.common.dto.SearchDto;
import com.study.common.paging.PagingResponse;
import com.study.domain.file.FileRequest;
import com.study.domain.file.FileResponse;
import com.study.domain.file.FileService;
import com.study.common.file.FileUtils;
import com.study.domain.member.MemberResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileService fileService;
    private final FileUtils fileUtils;

    // 게시글 작성 페이지
    @GetMapping("/post/write.do")
    public String openPostWrite(@RequestParam(value = "id", required = false) final Long id, Model model) {
        if (id != null) {
            PostResponse post = postService.findPostById(id);
            model.addAttribute("post", post);
        }
        return "post/write";
    }


    // 신규 게시글 생성
    @PostMapping("/post/save.do")
    public String savePost(final PostRequest params, HttpSession session, Model model) {

        // 1. 로그인한 사용자 정보에서 작성자 설정
        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        if (loginMember != null) {
            params.setWriter(loginMember.getLoginId());
        }

        // 2. 자동 가격 계산
        if (Boolean.TRUE.equals(params.getIsAutoPrice())) {
            try {
                int autoPrice = postService.calculateAutoPrice(
                        params.getProductName(),
                        params.getUsagePeriod(),
                        String.valueOf(params.getIsOpened()),
                        params.getCondition()
                );
                params.setPrice(autoPrice);
            } catch (Exception e) {
                params.setIsAutoPrice(false);
                e.printStackTrace();
            }
        }

        // 3. 게시글 저장 (→ postId 획득)
        Long postId = postService.savePost(params);

        // 4. 파일 업로드 (디스크에 저장)
        List<FileRequest> files = fileUtils.uploadFiles(params.getFiles());

        // 5. postId 주입
        for (FileRequest file : files) {
            file.setPostId(postId);
        }

        // 6. 파일 DB 저장
        fileService.saveFiles(postId, files);

        // 7. 성공 메시지 처리 후 리다이렉트
        MessageDto message = new MessageDto("게시글 생성이 완료되었습니다.", "/post/list.do", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }


    // 기존 게시글 수정
    @PostMapping("/post/update.do")
    public String updatePost(final PostRequest params, final SearchDto queryParams, HttpSession session, Model model) {

        // 로그인 사용자 정보에서 작성자 세팅
        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        if (loginMember != null) {
            params.setWriter(loginMember.getLoginId());
        }

        // 자동 가격 계산
        if (Boolean.TRUE.equals(params.getIsAutoPrice())) {
            try {
                int autoPrice = postService.calculateAutoPrice(
                        params.getProductName(),
                        params.getUsagePeriod(),
                        String.valueOf(params.getIsOpened()),
                        params.getCondition()
                );
                params.setPrice(autoPrice);
            } catch (Exception e) {
                params.setIsAutoPrice(false);
                e.printStackTrace();
            }
        }

        // 1. 게시글 정보 수정
        postService.updatePost(params);

        // 2. 파일 업로드 (to disk)
        List<FileRequest> uploadFiles = fileUtils.uploadFiles(params.getFiles());

        // 3. 파일 정보 저장 (to database)
        fileService.saveFiles(params.getId(), uploadFiles);

        // 4. 삭제할 파일 정보 조회 (from database)
        List<FileResponse> deleteFiles = fileService.findAllFileByIds(params.getRemoveFileIds());

        // 5. 파일 삭제 (from disk)
        fileUtils.deleteFiles(deleteFiles);

        // 6. 파일 삭제 (from database)
        fileService.deleteAllFileByIds(params.getRemoveFileIds());

        MessageDto message = new MessageDto("게시글 수정이 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
        return showMessageAndRedirect(message, model);
    }


    // 게시글 삭제
    @PostMapping("/post/delete.do")
    public String deletePost(@RequestParam final Long id, final SearchDto queryParams, Model model) {
        postService.deletePost(id);
        MessageDto message = new MessageDto("게시글 삭제가 완료되었습니다.", "/post/list.do", RequestMethod.GET, queryParamsToMap(queryParams));
        return showMessageAndRedirect(message, model);
    }

    // 게시글 리스트 페이지
    @GetMapping("/post/list.do")
    public String openPostList(@ModelAttribute("params") final SearchDto params, Model model) {
        PagingResponse<PostResponse> response = postService.findAllPost(params);
        model.addAttribute("response", response);
        return "post/list";
    }

    // 게시글 상세 페이지 열기
    @GetMapping("/post/view.do")
    public String openPostView(@RequestParam("id") Long id,
                               @RequestParam(required = false) Integer num,
                               Model model,
                               HttpSession session) {
        PostResponse post = postService.findPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("viewNum", num);

        MemberResponse loginMember = (MemberResponse) session.getAttribute("loginMember");
        model.addAttribute("loginMember", loginMember);

        return "post/view";
    }

    // 사용자에게 메시지를 전달하고 페이지 리다이렉트
    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "common/messageRedirect";
    }

    // 쿼리 스트링 파라미터를 Map에 담아 반환
    private Map<String, Object> queryParamsToMap(final SearchDto queryParams) {
        Map<String, Object> data = new HashMap<>();
        data.put("page", queryParams.getPage());
        data.put("recordSize", queryParams.getRecordSize());
        data.put("pageSize", queryParams.getPageSize());
        data.put("keyword", queryParams.getKeyword());
        data.put("searchType", queryParams.getSearchType());
        return data;
    }

}
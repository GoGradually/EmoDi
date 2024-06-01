package com.capstone.emodi.web.privatepost;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.privatepost.PrivatePost;
import com.capstone.emodi.exception.FileUploadException;
import com.capstone.emodi.exception.MemberNotFoundException;
import com.capstone.emodi.exception.PostNotFoundException;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.MemberService;
import com.capstone.emodi.service.PrivatePostService;
import com.capstone.emodi.web.dto.PrivatePostDto;
import com.capstone.emodi.web.response.ApiResponse;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/private/posts")
@RequiredArgsConstructor
public class PrivatePostController {
    private final PrivatePostService privatePostService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<PrivatePostDto>> createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                        @RequestBody PostString postString,
                                                        @RequestParam(required = false) MultipartFile image) {
        token = token.substring(7);
        String loginId = jwtTokenProvider.getLoginIdFromToken(token);

        String title = postString.title;
        String content = postString.content;
        Member member;
        List<String> keywords = new ArrayList<>(postString.keyword);
        try{
            member = memberService.findByLoginId(loginId);
        }catch (MemberNotFoundException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        }
        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("권한이 없습니다."));
        }

        // 입력 데이터 유효성 검사
        if (title.isEmpty() || content.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("제목 또는 내용이 비어 있습니다."));
        }
        try {
            PrivatePost post = privatePostService.createPrivatePost(title, content, image, member, keywords);
            PrivatePostDto retPost = new PrivatePostDto(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("게시글 생성 성공",retPost));
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PrivatePostDto>> updatePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                                  @PathVariable Long postId,
                                                                  @RequestBody PostString postString,
                                                                  @RequestParam (required = false) MultipartFile image) {

        token = token.substring(7);
        String loginId = jwtTokenProvider.getLoginIdFromToken(token);

        String title = postString.title;
        String content = postString.content;
        Member member;
        List<String> keywords = new ArrayList<>(postString.keyword);
        try{
            member = memberService.findByLoginId(loginId);
        }catch (MemberNotFoundException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        }
        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("권한이 없습니다."));
        }

        // 입력 데이터 유효성 검사
        if (title.isEmpty() || content.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("제목 또는 내용이 비어 있습니다."));
        }

        try {
            PrivatePost post = privatePostService.updatePrivatePost(postId, title, content, image, keywords);
            PrivatePostDto retPost = new PrivatePostDto(post);
            return ResponseEntity.ok(ApiResponse.success("게시글 업데이트 성공",retPost));
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    private ResponseEntity<ApiResponse<PrivatePostDto>> getPostResponseEntity(String accessToken, Long postId) {
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("권한이 없습니다."));
        }
        // 권한 체크
        String loginId = jwtTokenProvider.getLoginIdFromToken(accessToken);
        PrivatePost post = privatePostService.getPrivatePostById(postId);
        if (!post.getMember().getLoginId().equals(loginId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("접근 거부됨."));
        }
        return null;
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<PrivatePostDto>> deletePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                                        @PathVariable Long postId) {
        accessToken = accessToken.substring(7);
        // Access 토큰 유효성 검사
        ResponseEntity<ApiResponse<PrivatePostDto>> UNAUTHORIZED = getPostResponseEntity(accessToken, postId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        privatePostService.deletePrivatePost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 성공", null));
    }

    // 특정 회원이 작성한 게시글 목록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<PrivatePostDto>>> getPostsByMemberId(@PathVariable Long memberId) {
        List<PrivatePost> posts = privatePostService.getPrivatePostsByMemberId(memberId);
        List<PrivatePostDto> retPosts = posts.stream().map(PrivatePostDto::new).collect(Collectors.toList());;
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", retPosts));
    }

    // 특정 날짜에 작성된 게시글 목록 조회
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<PrivatePostDto>>> getPostsByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PrivatePost> posts = privatePostService.getPrivatePostsByDate(date);
        List<PrivatePostDto> retPosts = posts.stream().map(PrivatePostDto::new).collect(Collectors.toList());;
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", retPosts));
    }

    // 특정 사용자가 특정 날짜에 작성한 게시글 목록 조회
    @GetMapping("/member/{memberId}/date")
    public ResponseEntity<ApiResponse<List<PrivatePostDto>>> getPrivatePostsByMemberIdAndDate(@PathVariable Long memberId,
                                                                             @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PrivatePost> posts = privatePostService.getPrivatePostsByMemberIdAndDate(memberId, date);
        List<PrivatePostDto> retPosts = posts.stream().map(PrivatePostDto::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("조회 성공", retPosts));
    }




    public static class PostString{
        public String title;
        public String content;
        public List<String> keyword;
    }



    // PostNotFoundException 처리를 위한 ExceptionHandler 추가
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
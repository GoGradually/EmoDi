package com.capstone.emodi.web.post;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.exception.FileUploadException;
import com.capstone.emodi.exception.MemberNotFoundException;
import com.capstone.emodi.exception.PostNotFoundException;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.LikeService;
import com.capstone.emodi.service.MemberService;
import com.capstone.emodi.service.PostService;
import com.capstone.emodi.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final LikeService likeService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                           @RequestBody PostString postString,
                                           @RequestParam(required = false) MultipartFile image) {
        token = token.substring(7);
        String loginId = jwtTokenProvider.getLoginIdFromToken(token);

        String title = postString.title;
        String content = postString.content;
        Member member;
        try{
            member = memberService.findByLoginId(loginId);
        }catch (MemberNotFoundException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        if (!jwtTokenProvider.validateAccessToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 입력 데이터 유효성 검사
        if (title.isEmpty() || content.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {


            Post post = postService.createPost(title, content, image, member);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                           @PathVariable Long postId,
                                           @RequestBody PostString postString,
                                           @RequestParam (required = false) MultipartFile image) {

        accessToken = accessToken.substring(7);
        ResponseEntity<Post> UNAUTHORIZED = getPostResponseEntity(accessToken, postId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        String title = postString.title;
        String content = postString.content;

        // 입력 데이터 유효성 검사
        if (title.isEmpty() || content.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {

            Post post = postService.updatePost(postId, title, content, image);
            return ResponseEntity.ok(post);
        } catch (FileUploadException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private ResponseEntity<Post> getPostResponseEntity(String accessToken, Long postId) {
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // 권한 체크
        String loginId = jwtTokenProvider.getLoginIdFromToken(accessToken);
        Post post = postService.getPostById(postId);
        if (!post.getMember().getLoginId().equals(loginId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return null;
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Post> deletePost(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                           @PathVariable Long postId) {
        accessToken = accessToken.substring(7);
        // Access 토큰 유효성 검사
        ResponseEntity<Post> UNAUTHORIZED = getPostResponseEntity(accessToken, postId);
        if (UNAUTHORIZED != null) return UNAUTHORIZED;

        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // 특정 회원이 작성한 게시글 목록 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Post>> getPostsByMemberId(@PathVariable Long memberId) {
        List<Post> posts = postService.getPostsByMemberId(memberId);
        return ResponseEntity.ok(posts);
    }

    // 특정 날짜에 작성된 게시글 목록 조회
    @GetMapping("/date")
    public ResponseEntity<List<Post>> getPostsByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Post> posts = postService.getPostsByDate(date);
        return ResponseEntity.ok(posts);
    }

    // 특정 사용자가 특정 날짜에 작성한 게시글 목록 조회
    @GetMapping("/member/{memberId}/date")
    public ResponseEntity<List<Post>> getPostsByMemberIdAndDate(@PathVariable Long memberId,
                                                                @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Post> posts = postService.getPostsByMemberIdAndDate(memberId, date);
        return ResponseEntity.ok(posts);
    }




    public static class PostString{
        public String title;
        public String content;
    }


    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @RequestParam Long memberId) {
        likeService.likePost(postId, memberId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, @RequestParam Long memberId) {
        likeService.unlikePost(postId, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/likes")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long postId) {
        long likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(likeCount);
    }

    // PostNotFoundException 처리를 위한 ExceptionHandler 추가
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
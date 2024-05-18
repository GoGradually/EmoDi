package com.capstone.emodi.web.post;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.security.JwtTokenProvider;
import com.capstone.emodi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    private final MemberRepository memberService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestHeader("Authorization") String token,
                                           @RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           @RequestParam("image") MultipartFile image) {
        String loginId = jwtTokenProvider.getLoginIdFromToken(token.substring(7)); // "Bearer " 제거
        Optional<Member> member = memberService.findByLoginId(loginId);
        if(member.isEmpty()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String imagePath = saveImage(image);

        Post post = postService.createPost(title, content, imagePath, member.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId,
                                           @RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           @RequestParam(value = "image", required = false) MultipartFile image) {
        String imagePath = null;
        if (image != null) {
            imagePath = saveImage(image);
        }

        Post post = postService.updatePost(postId, title, content, imagePath);
        return ResponseEntity.ok(post);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
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

    // 이미지 저장 메서드
    private String saveImage(MultipartFile image) {
        try {
            // 이미지 파일 이름 생성
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // 이미지 파일 저장 경로 설정
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 이미지 파일 저장
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(image.getInputStream(), filePath);

            // 저장된 이미지 파일 경로 반환
            return uploadDir + uniqueFilename;
        } catch (IOException e) {
            // 예외 처리 로직 추가
            e.printStackTrace();
            return null;
        }
    }
}
// PostService.java
package com.capstone.emodi.service;

import com.capstone.emodi.domain.keyword.Keyword;
import com.capstone.emodi.domain.keyword.KeywordRepository;
import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import com.capstone.emodi.exception.FileUploadException;
import com.capstone.emodi.exception.PostNotFoundException;
import com.capstone.emodi.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final KeywordRepository keywordRepository;

    @Value("${postImage.dir}")
    String uploadDir;
    // 게시글 작성
    public Post createPost(String title, String content, byte[] imageBytes, Member member, List<String> keywordString) {
        String imagePath = null;
        if (imageBytes.length != 0) {
            try {
                imagePath = saveImage(imageBytes);
            } catch (IOException e) {
                throw new FileUploadException("이미지 파일 업로드 중 오류가 발생했습니다.");
            }
        } else {
            imagePath = "default-image.png";
        }
        Post post = Post.builder()
                .title(title)
                .content(content)
                .imagePath(imagePath)
                .member(member)
                .build();
        postRepository.save(post);
        List<Keyword> keywords = keywordString.stream().map(s->new Keyword(post, s)).toList();
        keywordRepository.saveAll(keywords);
        return post;
    }

    // 게시글 수정
    public Post updatePost(Long postId, String title, String content, byte[] imageBytes, List<String> keywordString) {
        String imagePath = null;
        if (imageBytes.length != 0) {
            try {
                imagePath = saveImage(imageBytes);
            } catch (IOException e) {
                throw new FileUploadException("이미지 파일 업로드 중 오류가 발생했습니다.");
            }
        }else {
            imagePath = "default-image.png";
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + postId));
        post.update(title, content, imagePath);
        keywordRepository.deleteByPost(post);
        List<Keyword> keywords = keywordString.stream().map(s->new Keyword(post, s)).toList();
        keywordRepository.saveAll(keywords);
        return post;
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + postId));
        postRepository.delete(post);
    }

    // 특정 회원이 작성한 게시글 목록 조회
    public List<Post> getPostsByMemberId(Long memberId) {
        return postRepository.findByMemberId(memberId);
    }
    
    // 게시글 id 로 게시글 조회
    public Post getPostById(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + postId));
    }

    // 특정 기간 내에 작성된 게시글 목록 조회
    public List<Post> getPostsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return postRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // 특정 날짜 이후에 작성된 게시글 목록 조회
    public List<Post> getPostsAfterDate(LocalDateTime date) {
        return postRepository.findByCreatedAtAfter(date);
    }

    // 특정 날짜 이전에 작성된 게시글 목록 조회
    public List<Post> getPostsBeforeDate(LocalDateTime date) {
        return postRepository.findByCreatedAtBefore(date);
    }

    public List<Post> getPostsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return postRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    // 특정 사용자가 특정 날짜에 작성한 게시글 목록 조회
    public List<Post> getPostsByMemberIdAndDate(Long memberId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return postRepository.findByMemberIdAndCreatedAtBetween(memberId, startOfDay, endOfDay);
    }
    // 이미지 저장 메서드
    private String saveImage(byte[] imageBytes) throws IOException {
        try {
            return FileUploadUtil.saveImage(imageBytes, uploadDir);
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 처리
            throw new RuntimeException("Failed to save image", e);
        }
    }
}
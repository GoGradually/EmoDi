package com.capstone.emodi.service;

import com.capstone.emodi.domain.Member;
import com.capstone.emodi.domain.PrivateKeyword;
import com.capstone.emodi.repository.PrivateKeywordRepository;
import com.capstone.emodi.domain.PrivatePost;
import com.capstone.emodi.repository.PrivatePostRepository;
import com.capstone.emodi.exception.FileUploadException;
import com.capstone.emodi.exception.PostNotFoundException;
import com.capstone.emodi.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PrivatePostService {
    private final PrivatePostRepository privatePostRepository;
    private final PrivateKeywordRepository privateKeywordRepository;

    @Value("${privatePostImage.dir}")
    String uploadDir;
    // 게시글 작성
    public PrivatePost createPrivatePost(String title, String content, byte[] imageBytes, Member member, List<String> keywordString) {
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
        PrivatePost privatePost = PrivatePost.builder()
                .title(title)
                .content(content)
                .imagePath(imagePath)
                .member(member)
                .build();
        privatePostRepository.save(privatePost);
        List<PrivateKeyword> keywords = keywordString.stream().map(s->new PrivateKeyword(privatePost, s)).toList();
        privateKeywordRepository.saveAll(keywords);
        return privatePost;
    }

    // 게시글 수정
    public PrivatePost updatePrivatePost(Long privatePostId, String title, String content, byte[] imageBytes, List<String> keywordString) {

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
        PrivatePost privatePost = privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + privatePostId));
        privatePost.update(title, content, imagePath);
        privateKeywordRepository.deleteByPrivatePost(privatePost);
        List<PrivateKeyword> keywords = keywordString.stream().map(s->new PrivateKeyword(privatePost, s)).toList();
        privateKeywordRepository.saveAll(keywords);
        return privatePost;
    }

    // 게시글 삭제
    public void deletePrivatePost(Long privatePostId) {
        PrivatePost privatePost = privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + privatePostId));
        privatePostRepository.delete(privatePost);
    }

    // 특정 회원이 작성한 게시글 목록 조회
    public List<PrivatePost> getPrivatePostsByMemberId(Long memberId) {
        return privatePostRepository.findByMemberId(memberId);
    }

    // 게시글 id 로 게시글 조회
    public PrivatePost getPrivatePostById(Long privatePostId){
        return privatePostRepository.findById(privatePostId)
                .orElseThrow(() -> new PostNotFoundException("해당 게시글이 없습니다. id=" + privatePostId));
    }

    // 특정 기간 내에 작성된 게시글 목록 조회
    public List<PrivatePost> getPrivatePostsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return privatePostRepository.findByCreatedAtBetween(startDate, endDate);
    }

    // 특정 날짜 이후에 작성된 게시글 목록 조회
    public List<PrivatePost> getPrivatePostsAfterDate(LocalDateTime date) {
        return privatePostRepository.findByCreatedAtAfter(date);
    }

    // 특정 날짜 이전에 작성된 게시글 목록 조회
    public List<PrivatePost> getPrivatePostsBeforeDate(LocalDateTime date) {
        return privatePostRepository.findByCreatedAtBefore(date);
    }

    public List<PrivatePost> getPrivatePostsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return privatePostRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    // 특정 사용자가 특정 날짜에 작성한 게시글 목록 조회
    public List<PrivatePost> getPrivatePostsByMemberIdAndDate(Long memberId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return privatePostRepository.findByMemberIdAndCreatedAtBetween(memberId, startOfDay, endOfDay);
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

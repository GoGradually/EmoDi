package com.capstone.emodi.web.post;

import com.capstone.emodi.domain.member.Member;
import com.capstone.emodi.domain.member.MemberRepository;
import com.capstone.emodi.domain.post.Post;
import com.capstone.emodi.domain.post.PostRepository;
import com.capstone.emodi.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Member member;
    private String accessToken;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .loginId("testuser")
                .username("테스트유저")
                .password("password")
                .email("test@example.com")
                .tellNumber("01012345678")
                .build();
        memberRepository.save(member);

        accessToken = generateAccessToken(member);
    }

    private String generateAccessToken(Member member) {
        String token = jwtTokenProvider.generateAccessToken(member.getLoginId());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member.getLoginId(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return token;
    }

    @Test
    void createPost_success() throws Exception {
        // given
        String title = "테스트 제목";
        String content = "테스트 내용";

        // when, then
        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .content("{\"title\":\"" + title + "\", \"content\":\"" + content + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    void updatePost_success() throws Exception {
        // given
        Post post = Post.builder()
                .title("기존 제목")
                .content("기존 내용")
                .member(member)
                .build();
        postRepository.save(post);

        String updatedTitle = "수정된 제목";
        String updatedContent = "수정된 내용";

        // when, then
        mockMvc.perform(put("/api/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .content("{\"title\":\"" + updatedTitle + "\", \"content\":\"" + updatedContent + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedTitle))
                .andExpect(jsonPath("$.content").value(updatedContent));
    }

    @Test
    void deletePost_success() throws Exception {
        // given
        Post post = Post.builder()
                .title("삭제할 게시글")
                .content("삭제할 내용")
                .member(member)
                .build();
        postRepository.save(post);

        // when, then
        mockMvc.perform(delete("/api/posts/{postId}", post.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Assertions.assertThat(postRepository.findById(post.getId())).isEmpty();
    }

    @Test
    void deletePost_failWithNotFoundPost() throws Exception {
        // given
        Long nonExistentPostId = 9999L;

        // when, then
        mockMvc.perform(delete("/api/posts/{postId}", nonExistentPostId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPostsByMemberId_success() throws Exception {
        // given
        Post post1 = Post.builder()
                .title("게시글 1")
                .content("내용 1")
                .member(member)
                .build();
        Post post2 = Post.builder()
                .title("게시글 2")
                .content("내용 2")
                .member(member)
                .build();
        postRepository.saveAll(List.of(post1, post2));

        // when, then
        mockMvc.perform(get("/api/posts/member/{memberId}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(post1.getContent()))
                .andExpect(jsonPath("$[1].title").value(post2.getTitle()))
                .andExpect(jsonPath("$[1].content").value(post2.getContent()));
    }

    @Test
    void getPostsByDate_success() throws Exception {
        // given
        LocalDateTime fixedDate = LocalDateTime.of(2023, 5, 19, 0, 0);
        Post post1 = Post.builder()
                .title("게시글 1")
                .content("내용 1")
                .member(member)
                .createdAt(fixedDate)
                .build();
        postRepository.save(post1);

        String fixedDateString = fixedDate.toLocalDate().toString();

        // when, then
        mockMvc.perform(get("/api/posts/date")
                        .param("date", fixedDateString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(post1.getContent()));
    }

    @Test
    void getPostsByMemberIdAndDate_success() throws Exception {
        // given
        LocalDateTime fixedDate = LocalDateTime.of(2023, 5, 19, 0, 0);
        Post post1 = Post.builder()
                .title("게시글 1")
                .content("내용 1")
                .member(member)
                .createdAt(fixedDate)
                .build();
        postRepository.save(post1);

        String fixedDateString = fixedDate.toLocalDate().toString();

        // when, then
        mockMvc.perform(get("/api/posts/member/{memberId}/date", member.getId())
                        .param("date", fixedDateString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(post1.getTitle()))
                .andExpect(jsonPath("$[0].content").value(post1.getContent()));
    }
}

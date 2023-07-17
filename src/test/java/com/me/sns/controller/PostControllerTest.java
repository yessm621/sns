package com.me.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.sns.controller.request.PostCreateRequest;
import com.me.sns.controller.request.PostModifyRequest;
import com.me.sns.exception.ErrorCode;
import com.me.sns.exception.SnsApplicationException;
import com.me.sns.fixture.PostEntityFixture;
import com.me.sns.model.Post;
import com.me.sns.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser
    void 포스트작성() throws Exception {
        String title = "title";
        String body = "body";

        mockMvc.perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
        // 로그인하지 않은 경우
    void 포스트_작성시_로그인하지_않은_경우() throws Exception {
        String title = "title";
        String body = "body";

        mockMvc.perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트_수정() throws Exception {
        String title = "new title";
        String body = "new body";

        when(postService.modify(eq(title), eq(body), any(), any()))
                .thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1, 1)));

        mockMvc.perform(
                        put("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트_수정시_로그인하지_않은_경우() throws Exception {
        String title = "new title";
        String body = "new body";

        mockMvc.perform(
                        put("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트_수정시_본인이_작성한_글이_아니라면_에러발생() throws Exception {
        String title = "new title";
        String body = "new body";

        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION))
                .when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(
                        put("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트_수정시_수정하려는_글이_없는_경우() throws Exception {
        String title = "new title";
        String body = "new body";

        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUNT))
                .when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(
                        put("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 포스트_삭제() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트_삭제시_로그인하지_않은_경우() throws Exception {
        mockMvc.perform(
                        delete("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트_삭제시_작성자와_삭제요청자가_다를_경우() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

        mockMvc.perform(
                        delete("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 포스트_삭제시_삭제하려는_포스트가_존재하지_않을_경우() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUNT)).when(postService).delete(any(), any());

        mockMvc.perform(
                        delete("/api/v1/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void 피드목록() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(
                        get("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 피드목록_요청시_로그인하지_않은_경우() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(
                        get("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void 내피드목록() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(
                        get("/api/v1/posts/my")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 내피드목록_요청시_로그인하지_않은_경우() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(
                        get("/api/v1/posts/my")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
}

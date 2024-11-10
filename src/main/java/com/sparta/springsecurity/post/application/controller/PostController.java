package com.sparta.springsecurity.post.application.controller;

import com.sparta.springsecurity.auth.infrastructure.UserDetailsImpl;
import com.sparta.springsecurity.post.application.dto.PostRequestDto;
import com.sparta.springsecurity.post.application.dto.PostResponseDto;
import com.sparta.springsecurity.post.domain.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 권한 확인을 위한 post 작성, 조회 controller

@Tag(name = "Post API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService; // PostService는 글 작성 및 조회를 담당

    // 글 작성 - admin만 가능
    @Operation(summary = "Post 작성", description = "ADMIN만 작성 가능")
    @PreAuthorize("hasRole('ROLE_ADMIN')")// ADMIN 권한만 허용
    @PostMapping
    public PostResponseDto createPost(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(postRequestDto);
    }

    // 글 전체 조회 - user, admin 가능
    @Operation(summary = "Post 조회", description = "ADMIN, USER 조회 가능")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping
    public List<PostResponseDto> getPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPosts();
    }
}

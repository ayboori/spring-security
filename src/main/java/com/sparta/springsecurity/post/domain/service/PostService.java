package com.sparta.springsecurity.post.domain.service;

import com.sparta.springsecurity.post.application.dto.PostRequestDto;
import com.sparta.springsecurity.post.application.dto.PostResponseDto;
import com.sparta.springsecurity.post.domain.entity.Post;
import com.sparta.springsecurity.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponseDto createPost(PostRequestDto requestDto) {
        // 게시물 생성
        Post post = new Post(requestDto);

        // 게시물 저장
        postRepository.save(post);

        // 저장된 게시물을 PostResponseDto로 변환하여 반환
        return new PostResponseDto(post);
    }

    public List<PostResponseDto> getPosts() {
        List<Post> posts = postRepository.findAll();

        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        for(Post post : posts){
            postResponseDtos.add(new PostResponseDto(post));
        }

        return postResponseDtos;
    }
}

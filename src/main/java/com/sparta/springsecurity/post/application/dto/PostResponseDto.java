package com.sparta.springsecurity.post.application.dto;

import com.sparta.springsecurity.post.domain.entity.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDto {
    private Long id;
    private String text;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.text = post.getText();
    }
}

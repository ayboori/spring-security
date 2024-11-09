package com.sparta.springsecurity.post.domain.entity;

import com.sparta.springsecurity.post.application.dto.PostRequestDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
@Table(name = "p_post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String text;

    public Post(PostRequestDto requestDto) {
        this.text = requestDto.getText();
    }
}
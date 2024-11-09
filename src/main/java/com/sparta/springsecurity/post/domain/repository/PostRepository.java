package com.sparta.springsecurity.post.domain.repository;

import com.sparta.springsecurity.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}

package com.toyproject.webservice.web.dto;

import com.toyproject.webservice.domain.posts.Posts;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PostsResponseDto {

    private Long id;

    private String title;

    private String content;

    private String author;

    public PostsResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}

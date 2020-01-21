package com.toyproject.webservice.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.webservice.domain.posts.Posts;
import com.toyproject.webservice.domain.posts.PostsRepository;
import com.toyproject.webservice.web.dto.PostsSaveRequestDto;
import com.toyproject.webservice.web.dto.PostsUpdateRequestDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTestr {

    @LocalServerPort
    private int port;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before //매번 테스트가 시작되기 전에 MockMvc 인스턴스 생성
    public void setUp(){
        mockMvc = MockMvcBuilders
                    .webAppContextSetup(webApplicationContext)
                    .apply(springSecurity())
                    .build();
    }
    @Test
    @WithMockUser(roles = "USER") //withMochUser는 MockMVC에서만 동작
    public void Posts_등록된다() throws Exception {
        //given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                                        .title(title)
                                        .content(content)
                                        .author("author")
                                        .build();
        String url = "http://localhost:"+port+"/api/v1/posts";

        //when
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);


    }

    @Test
    @WithMockUser(roles = "USER") //withMochUser는 MockMVC에서만 동작
    public void Posts_수정된다() throws Exception {
        //given
        Posts savePosts = postsRepository.save(Posts.builder()
                                                .title("title")
                                                .content("content")
                                                .author("author")
                                                .build()) ;
        Long updateId = savePosts.getId();
        String expectedTitle = "title";
        String expectedContent = "content";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                                                                .title(expectedTitle)
                                                                .content(expectedContent)
                                                                .build();
        String url = "http://localhost:"+port+"/api/v1/posts/"+updateId;


        //when
        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);


    }


}

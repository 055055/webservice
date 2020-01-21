package com.toyproject.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaAuditing 제거. @WebMvcTest시 해당 어노테이션 읽어서 Entitiy없다고 에러 발생.
@SpringBootApplication
public class WebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
    }

}

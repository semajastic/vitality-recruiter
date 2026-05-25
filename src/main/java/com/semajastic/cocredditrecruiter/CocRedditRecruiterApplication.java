package com.semajastic.cocredditrecruiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class CocRedditRecruiterApplication {

  public static void main(String[] args) {
    log.info("Starting Coc Reddit Recruiter");
    SpringApplication.run(CocRedditRecruiterApplication.class, args);
  }
}

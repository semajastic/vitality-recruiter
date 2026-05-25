package com.semajastic.vitalityrecruiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class VitalityRecruiterApplication{

  public static void main(String[] args) {
    log.info("Starting Vitality Recruiter");
    SpringApplication.run(VitalityRecruiterApplication.class, args);
  }
}

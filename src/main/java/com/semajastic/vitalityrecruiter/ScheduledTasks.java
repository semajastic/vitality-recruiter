package com.semajastic.vitalityrecruiter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.semajastic.vitalityrecruiter.service.RecruitService;
import com.semajastic.vitalityrecruiter.service.RecruitValidationException;

@Slf4j
@Component
public class ScheduledTasks {

  @Autowired private RecruitService recruitService;

  @Scheduled(cron = "${app-config.cron-post-reddit}", zone = "${app-config.zone}")
  public void postReddit() {
    log.info("Scheduled Reddit post task started");
    this.recruitService
        .postReddit()
        .subscribe(
            post -> log.info("Reddit post submitted successfully: {}", post.getJson().getData().getId()),
            error -> {
              if (error instanceof RecruitValidationException) {
                log.warn("Reddit post skipped: {}", error.getMessage());
              } else {
                log.error("Reddit post failed: {}", error.getMessage(), error);
              }
            });
  }
}

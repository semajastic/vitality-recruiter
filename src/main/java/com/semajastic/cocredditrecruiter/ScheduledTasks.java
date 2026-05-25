package com.semajastic.cocredditrecruiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

  @Scheduled(cron = "${app-config.cron-hello}", zone = "${app-config.zone}")
  public void printHello() {
    log.info("Scheduled hello task executed");
  }
}

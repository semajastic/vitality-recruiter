package com.semajastic.cocredditrecruiter.clashhitters.service;

import com.semajastic.cocredditrecruiter.clashhitters.config.ClashHittersConfigProperties;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersRecruitStats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ClashHittersService {

  @Autowired
  private ClashHittersConfigProperties clashHittersConfigProperties;

  @Autowired
  @Qualifier("clashhitters-client")
  private WebClient clashHittersWebClient;

  public Mono<ClashHittersRecruitStats> getRecruitStatsData() {
    String clanTag = this.clashHittersConfigProperties.getClanTag();

    log.debug("Fetching recruit stats for clan {}", clanTag);

    return this.clashHittersWebClient
        .get()
        .uri(this.clashHittersConfigProperties.getRecruitStatsUri())
        .header("clanTag", clanTag)
        .retrieve()
        .bodyToMono(ClashHittersRecruitStats.class)
        .doOnSuccess(stats -> log.debug("Recruit stats fetched for clan {}", clanTag))
        .doOnError(error -> log.error("Failed to fetch recruit stats for clan {}", clanTag, error));
  }

}

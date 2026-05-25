package com.semajastic.vitalityrecruiter.clashhitters.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "clashhitters")
@Data
public class ClashHittersConfigProperties {

  private String clanTag;
  private String baseUrl;
  private String recruitStatsUri;

}

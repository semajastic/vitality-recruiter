package com.semajastic.cocredditrecruiter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app-config")
@Data
public class AppConfigProperties {

  private String cronHello;
  private String zone;
  private Integer clanMaxSize;
  private Integer cwlBlackoutStartDay;
  private Integer cwlBlackoutEndDay;

}

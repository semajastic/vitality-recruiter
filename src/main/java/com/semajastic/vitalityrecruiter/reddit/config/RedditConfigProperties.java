package com.semajastic.vitalityrecruiter.reddit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "reddit")
@Data
public class RedditConfigProperties {

  private String tokenBaseUrl;
  private String tokenUri;
  private String grantType;
  private String submitBaseUrl;
  private String submitUri;
  private String subreddit;
  private String username;
  private String password;
  private String clientId;
  private String clientSecret;

}

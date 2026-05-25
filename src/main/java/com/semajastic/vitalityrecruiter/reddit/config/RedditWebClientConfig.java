package com.semajastic.vitalityrecruiter.reddit.config;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Slf4j
@Configuration
public class RedditWebClientConfig {

  @Autowired
  private RedditConfigProperties redditConfigProperties;

  @Bean("reddit-token-client")
  public WebClient tokenWebClient() {
    log.info(
        "Creating Reddit token WebClient with base URL {}",
        this.redditConfigProperties.getTokenBaseUrl());

    HttpClient httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

    return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(this.redditConfigProperties.getTokenBaseUrl())
        .defaultHeader("User-Agent", this.redditConfigProperties.getUserAgent())
        .build();
  }

  @Bean("reddit-submit-client")
  public WebClient submitWebClient() {
    log.info(
        "Creating Reddit submit WebClient with base URL {}",
        this.redditConfigProperties.getSubmitBaseUrl());

    HttpClient httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

    return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(this.redditConfigProperties.getSubmitBaseUrl()).build();
  }
}

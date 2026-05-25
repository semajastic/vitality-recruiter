package com.semajastic.vitalityrecruiter.clashhitters.config;

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
public class ClashHittersWebClientConfig {

  @Autowired
  private ClashHittersConfigProperties clashHittersConfigProperties;

  @Bean("clashhitters-client")
  public WebClient clashHittersWebClient() {
    log.info(
        "Creating ClashHitters WebClient with base URL {}",
        this.clashHittersConfigProperties.getBaseUrl());

    HttpClient httpClient = HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

    return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(this.clashHittersConfigProperties.getBaseUrl())
        .build();
  }

}

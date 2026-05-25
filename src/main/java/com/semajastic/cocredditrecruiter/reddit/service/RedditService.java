package com.semajastic.cocredditrecruiter.reddit.service;

import com.semajastic.cocredditrecruiter.reddit.config.RedditConfigProperties;
import com.semajastic.cocredditrecruiter.reddit.model.RedditAccessToken;
import com.semajastic.cocredditrecruiter.reddit.model.RedditPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RedditService {

  @Autowired private RedditConfigProperties redditConfigProperties;

  @Autowired
  @Qualifier("reddit-token-client")
  private WebClient tokenWebClient;

  @Autowired
  @Qualifier("reddit-submit-client")
  private WebClient submitWebClient;

  public Mono<RedditAccessToken> getToken(
      String accountId, String accountSecret, String clientId, String clientSecret) {

    LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

    formData.add("grant_type", this.redditConfigProperties.getGrantType());
    formData.add("username", accountId);
    formData.add("password", accountSecret);

    log.debug("Requesting Reddit access token for user {}", accountId);

    return this.tokenWebClient
        .post()
        .uri(this.redditConfigProperties.getTokenUri())
        .headers(h -> h.setBasicAuth(clientId, clientSecret))
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .bodyToMono(RedditAccessToken.class)
        .doOnSuccess(token -> log.debug("Reddit access token obtained for user {}", accountId))
        .doOnError(
            error ->
                log.error("Failed to obtain Reddit access token for user {}", accountId, error));
  }

  public Mono<RedditPost> submitPost(String token, String subreddit, String title, String text) {

    LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("title", title);
    formData.add("text", text);
    formData.add("sr", subreddit);
    formData.add("kind", "self");
    formData.add("api_type", "json");
    formData.add("resubmit", "true");
    formData.add("sendreplies", "true");

    log.debug("Submitting post to r/{} with title {}", subreddit, title);

    return this.submitWebClient
        .post()
        .uri(this.redditConfigProperties.getSubmitUri())
        .headers(h -> h.setBearerAuth(token))
        .body(BodyInserters.fromFormData(formData))
        .retrieve()
        .bodyToMono(RedditPost.class)
        .doOnSuccess(post -> log.debug("Post submitted to r/{} with title {}", subreddit, title))
        .doOnError(
            error -> log.error("Failed to submit post to r/{} with title {}", subreddit, title, error));
  }
}

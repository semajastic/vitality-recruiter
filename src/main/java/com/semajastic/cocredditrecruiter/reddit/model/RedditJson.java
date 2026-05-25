package com.semajastic.cocredditrecruiter.reddit.model;

import java.util.List;
import lombok.Data;

@Data
public class RedditJson {

  private List<Object> errors;
  private RedditData data;

}

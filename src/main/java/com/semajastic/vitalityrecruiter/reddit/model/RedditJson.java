package com.semajastic.vitalityrecruiter.reddit.model;

import java.util.List;
import lombok.Data;

@Data
public class RedditJson {

  private List<Object> errors;
  private RedditData data;

}

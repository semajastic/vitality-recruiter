package com.semajastic.vitalityrecruiter.clashhitters.model;

import lombok.Data;

@Data
public class ClashHittersClanGame {

  private Long id;
  private String clanTag;
  private String startDate;
  private String endDate;
  private Integer goal;
  private Integer score;
  private Boolean hasTopScore;
  private String imageUrl;
  private String startTimeDisplay;
  private String endTimeDisplay;
  private String createdOn;
  private String lastUpdatedOn;

}

package com.semajastic.cocredditrecruiter.clashhitters.model;

import lombok.Data;

@Data
public class ClashHittersRaid {

  private Long id;
  private String clanTag;
  private String startTime;
  private String endTime;
  private String state;
  private Integer raidsCompleted;
  private Integer actualRaidsCompleted;
  private Integer totalAttacks;
  private Integer enemyDistrictsDestroyed;
  private Integer offensiveReward;
  private Integer defensiveReward;
  private Integer capitalTotalLoot;
  private Integer totalRewardIf6Attacks;
  private Boolean hasHighestTotalReward;
  private String startTimeDisplay;
  private String endTimeDisplay;
  private String createdOn;
  private String lastUpdatedOn;

}

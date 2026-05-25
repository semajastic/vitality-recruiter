package com.semajastic.cocredditrecruiter.clashhitters.model;

import lombok.Data;

@Data
public class ClashHittersWar {

  private Long id;
  private String result;
  private String endTime;
  private Integer teamSize;
  private Integer attacksPerMember;
  private String clanTag;
  private String clanName;
  private Integer clanLevel;
  private Integer clanAttacks;
  private Integer clanStars;
  private Double clanDestructionPercentage;
  private Integer clanExpEarned;
  private String clanSmallBadgeUrl;
  private String clanMediumBadgeUrl;
  private String clanLargeBadgeUrl;
  private String opponentTag;
  private String opponentName;
  private Integer opponentLevel;
  private Integer opponentAttacks;
  private Integer opponentStars;
  private Double opponentDestructionPercentage;
  private Integer opponentExpEarned;
  private String opponentSmallBadgeUrl;
  private String opponentMediumBadgeUrl;
  private String opponentLargeBadgeUrl;
  private String createdOn;
  private String lastUpdatedOn;
  private String endTimeDisplay;
  private String cwlSeason;

}

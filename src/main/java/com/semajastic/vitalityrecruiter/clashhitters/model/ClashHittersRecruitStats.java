package com.semajastic.vitalityrecruiter.clashhitters.model;

import java.util.List;
import lombok.Data;

@Data
public class ClashHittersRecruitStats {

  private String clanTag;
  private String clanName;
  private Integer clanLevel;
  private Integer memberCount;
  private Integer requiredTownHall;
  private String clanWarLeague;
  private Integer capitalHallLevel;
  private String clanCapitalLeague;
  private List<ClashHittersWar> wars;
  private List<ClashHittersClanGame> clanGames;
  private List<ClashHittersRaid> raids;

}

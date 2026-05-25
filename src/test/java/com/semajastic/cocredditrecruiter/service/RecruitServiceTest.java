package com.semajastic.cocredditrecruiter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersClanGame;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersRaid;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersRecruitStats;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersWar;
import com.semajastic.cocredditrecruiter.config.AppConfigProperties;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecruitServiceTest {

  @Mock private AppConfigProperties appConfigProperties;

  @InjectMocks private RecruitService recruitService;

  @BeforeEach
  void setUp() throws IOException {
    when(this.appConfigProperties.getZone()).thenReturn("America/New_York");
    this.recruitService.init();
  }

  @Test
  void renderMarkdown_substitutesPlaceholdersAndFormatsLogs() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    String markdown = this.recruitService.renderMarkdown(stats);

    assertThat(markdown).startsWith("[Recruiting] Vitality");
    assertThat(markdown).contains("Welcome to Vitality!");
    assertThat(markdown).doesNotContain("{{clan_name}}");
    assertThat(markdown).contains("https://vitality.clashhitters.com");
    assertThat(markdown).contains("| 5/21/26 | 25 | 73 | 98.8% | Won |");
    assertThat(markdown).contains("| 5/15/26 | 5/18/26 | 143 | 587,250 | 1,496 |");
    assertThat(markdown).contains("| 4/22/26 | 4/28/26 | 50,000 | 121,450 |");
  }

  @Test
  void renderMarkdown_formatsCwlWarWithNullResult() {
    ClashHittersWar cwlWar = new ClashHittersWar();
    cwlWar.setEndTime("20260510T043454.000Z");
    cwlWar.setTeamSize(15);
    cwlWar.setClanStars(290);
    cwlWar.setClanDestructionPercentage(598.73334);
    cwlWar.setResult(null);

    ClashHittersRecruitStats stats = new ClashHittersRecruitStats();
    stats.setClanName("Vitality");
    stats.setClanTag("#8VV2CGYY");
    stats.setClanLevel(29);
    stats.setRequiredTownHall(12);
    stats.setClanWarLeague("Master League III");
    stats.setCapitalHallLevel(10);
    stats.setClanCapitalLeague("Champion League III");
    stats.setWars(List.of(cwlWar));
    stats.setRaids(List.of());
    stats.setClanGames(List.of());

    String markdown = this.recruitService.renderMarkdown(stats);

    assertThat(markdown).contains("| 5/10/26 | 15 | 290 | 598.7% | May 2026 CWL |");
  }

  private ClashHittersRecruitStats buildFixtureStats() {
    ClashHittersWar war = new ClashHittersWar();
    war.setEndTime("20260522T014440.000Z");
    war.setTeamSize(25);
    war.setClanStars(73);
    war.setClanDestructionPercentage(98.76);
    war.setResult("win");

    ClashHittersRaid raid = new ClashHittersRaid();
    raid.setStartTime("20260515T070000.000Z");
    raid.setEndTime("20260518T070000.000Z");
    raid.setTotalAttacks(143);
    raid.setCapitalTotalLoot(587250);
    raid.setTotalRewardIf6Attacks(1496);

    ClashHittersClanGame clanGame = new ClashHittersClanGame();
    clanGame.setStartDate("20260422T080000.000Z");
    clanGame.setEndDate("20260428T080000.000Z");
    clanGame.setGoal(50000);
    clanGame.setScore(121450);

    ClashHittersRecruitStats stats = new ClashHittersRecruitStats();
    stats.setClanName("Vitality");
    stats.setClanTag("#8VV2CGYY");
    stats.setClanLevel(29);
    stats.setRequiredTownHall(12);
    stats.setClanWarLeague("Master League III");
    stats.setCapitalHallLevel(10);
    stats.setClanCapitalLeague("Champion League III");
    stats.setWars(List.of(war));
    stats.setRaids(List.of(raid));
    stats.setClanGames(List.of(clanGame));
    return stats;
  }
}

package com.semajastic.vitalityrecruiter.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.semajastic.vitalityrecruiter.clashhitters.model.ClashHittersClanGame;
import com.semajastic.vitalityrecruiter.clashhitters.model.ClashHittersRaid;
import com.semajastic.vitalityrecruiter.clashhitters.model.ClashHittersRecruitStats;
import com.semajastic.vitalityrecruiter.clashhitters.model.ClashHittersWar;
import com.semajastic.vitalityrecruiter.clashhitters.service.ClashHittersService;
import com.semajastic.vitalityrecruiter.config.AppConfigProperties;
import com.semajastic.vitalityrecruiter.reddit.config.RedditConfigProperties;
import com.semajastic.vitalityrecruiter.reddit.model.RedditAccessToken;
import com.semajastic.vitalityrecruiter.reddit.model.RedditPost;
import com.semajastic.vitalityrecruiter.reddit.service.RedditService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecruitServiceTest {

  @Mock private ClashHittersService clashHittersService;
  @Mock private RedditService redditService;
  @Mock private AppConfigProperties appConfigProperties;
  @Mock private RedditConfigProperties redditConfigProperties;

  @InjectMocks private RecruitService recruitService;

  @BeforeEach
  void setUp() throws IOException {
    when(this.appConfigProperties.getZone()).thenReturn("America/New_York");
    when(this.appConfigProperties.getClanMaxSize()).thenReturn(50);
    when(this.appConfigProperties.getCwlBlackoutStartDay()).thenReturn(1);
    when(this.appConfigProperties.getCwlBlackoutEndDay()).thenReturn(10);
    this.recruitService.init();
  }

  @Test
  void renderMarkdown_substitutesPlaceholdersAndFormatsLogs() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    String markdown = this.recruitService.renderMarkdown(stats);

    assertThat(markdown).startsWith("[Recruiting] Vitality");
    assertThat(markdown).contains("\n\n# 🏂 Welcome to Vitality!");
    assertThat(markdown).contains("Welcome to Vitality!");
    assertThat(markdown).doesNotContain("{{clan_name}}");
    assertThat(markdown).contains("https://vitality.clashhitters.com");
    assertThat(markdown).contains("| 5/21/26 | 25 | 73 | 98.8% | Won |");
    assertThat(markdown).contains("| 5/15/26 | 5/18/26 | 143 | 587,250 | 1,496 |");
    assertThat(markdown).contains("| 4/22/26 | 4/28/26 | 50,000 | 121,450 |");
  }

  @Test
  void renderPostContent_returnsSeparateTitleAndText() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    RecruitService.RenderedPost post = this.recruitService.renderPostContent(stats);

    assertThat(post.title()).isEqualTo("[Recruiting] Vitality | #8VV2CGYY | Townhall 12+ | Clan Level 29 | Donation, Wars, CWL, Raids & Clan Games");
    assertThat(post.text()).startsWith("# 🏂 Welcome to Vitality!");
    assertThat(post.text()).doesNotContain("[Recruiting] Vitality |");
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

  @Test
  void validateRecruitAllowed_throwsWhenClanIsFull() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    stats.setMemberCount(50);

    assertThatThrownBy(() -> this.recruitService.validateClanNotFull(stats))
        .isInstanceOf(RecruitValidationException.class)
        .hasMessage("Clan is full");
  }

  @Test
  void validateNotCwlBlackout_throwsDuringBlackoutDays() {
    assertThatThrownBy(() -> this.recruitService.validateNotCwlBlackout(LocalDate.of(2026, 5, 5)))
        .isInstanceOf(RecruitValidationException.class)
        .hasMessage("CWL is in progress and recruit is disabled");
  }

  @Test
  void validateNotCwlBlackout_allowsOutsideBlackoutDays() {
    this.recruitService.validateNotCwlBlackout(LocalDate.of(2026, 5, 15));
  }

  @Test
  void postReddit_submitsSubstitutedTitleAndText() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    stats.setMemberCount(49);

    RedditAccessToken token = new RedditAccessToken();
    token.setAccessToken("test-token");
    RedditPost redditPost = new RedditPost();

    when(this.clashHittersService.getRecruitStatsData()).thenReturn(Mono.just(stats));
    when(this.redditConfigProperties.getUsername()).thenReturn("user");
    when(this.redditConfigProperties.getPassword()).thenReturn("pass");
    when(this.redditConfigProperties.getClientId()).thenReturn("client-id");
    when(this.redditConfigProperties.getClientSecret()).thenReturn("client-secret");
    when(this.redditConfigProperties.getSubreddit()).thenReturn("ClashOfClansRecruit");
    when(this.redditService.getToken("user", "pass", "client-id", "client-secret"))
        .thenReturn(Mono.just(token));
    when(this.redditService.submitPost(
            eq("test-token"),
            eq("ClashOfClansRecruit"),
            eq("[Recruiting] Vitality | #8VV2CGYY | Townhall 12+ | Clan Level 29 | Donation, Wars, CWL, Raids & Clan Games"),
            org.mockito.ArgumentMatchers.argThat(
                text ->
                    text.startsWith("# 🏂 Welcome to Vitality!")
                        && !text.startsWith("[Recruiting]"))))
        .thenReturn(Mono.just(redditPost));

    this.recruitService
        .validateNotCwlBlackout(LocalDate.of(2026, 5, 15));

    StepVerifier.create(this.recruitService.postReddit()).expectNext(redditPost).verifyComplete();

    verify(this.redditService)
        .submitPost(
            eq("test-token"),
            eq("ClashOfClansRecruit"),
            eq("[Recruiting] Vitality | #8VV2CGYY | Townhall 12+ | Clan Level 29 | Donation, Wars, CWL, Raids & Clan Games"),
            org.mockito.ArgumentMatchers.argThat(
                text ->
                    text.startsWith("# 🏂 Welcome to Vitality!")
                        && !text.contains("[Recruiting] Vitality | #8VV2CGYY | Townhall 12+ | Clan Level 29 | Donation, Wars, CWL, Raids & Clan Games\n\n#")));
  }

  @Test
  void postReddit_failsWhenClanIsFull() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    stats.setMemberCount(50);

    when(this.clashHittersService.getRecruitStatsData()).thenReturn(Mono.just(stats));

    StepVerifier.create(this.recruitService.postReddit())
        .expectErrorMatches(
            e ->
                e instanceof RecruitValidationException
                    && e.getMessage().equals("Clan is full"))
        .verify();
  }

  @Test
  void postReddit_failsWhenRedditCredentialsMissing() {
    ClashHittersRecruitStats stats = buildFixtureStats();
    stats.setMemberCount(49);

    when(this.clashHittersService.getRecruitStatsData()).thenReturn(Mono.just(stats));
    when(this.redditConfigProperties.getUsername()).thenReturn("");
    when(this.redditConfigProperties.getPassword()).thenReturn("pass");
    when(this.redditConfigProperties.getClientId()).thenReturn("client-id");
    when(this.redditConfigProperties.getClientSecret()).thenReturn("client-secret");
    when(this.redditConfigProperties.getSubreddit()).thenReturn("ClashOfClansRecruit");

    this.recruitService.validateNotCwlBlackout(LocalDate.of(2026, 5, 15));

    StepVerifier.create(this.recruitService.postReddit())
        .expectErrorMatches(
            e ->
                e instanceof RecruitValidationException
                    && e.getMessage().equals("Reddit credentials are not configured"))
        .verify();
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
    stats.setMemberCount(49);
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

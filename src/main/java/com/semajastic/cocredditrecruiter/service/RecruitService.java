package com.semajastic.cocredditrecruiter.service;

import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersClanGame;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersRaid;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersRecruitStats;
import com.semajastic.cocredditrecruiter.clashhitters.model.ClashHittersWar;
import com.semajastic.cocredditrecruiter.clashhitters.service.ClashHittersService;
import com.semajastic.cocredditrecruiter.config.AppConfigProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RecruitService {

  private static final DateTimeFormatter COC_UTC_FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS'Z'").withZone(ZoneId.of("UTC"));
  private static final String EMPTY_LOG_ROW = "| - | - | - | - | - |";

  @Autowired private ClashHittersService clashHittersService;
  @Autowired private AppConfigProperties appConfigProperties;

  private String titleTemplate;
  private String textTemplate;
  private ZoneId zoneId;
  private DateTimeFormatter displayDateFormat;

  @PostConstruct
  void init() throws IOException {
    this.titleTemplate = readClasspathResource("reddit-post-title.md");
    this.textTemplate = readClasspathResource("reddit-post-text.md");
    this.zoneId = ZoneId.of(this.appConfigProperties.getZone());
    this.displayDateFormat = DateTimeFormatter.ofPattern("M/d/yy").withZone(this.zoneId);
  }

  public Mono<String> buildPreviewMarkdown() {
    return this.clashHittersService.getRecruitStatsData().map(this::renderMarkdown);
  }

  String renderMarkdown(ClashHittersRecruitStats stats) {
    Map<String, String> substitutions = buildSubstitutions(stats);
    String title = substitute(this.titleTemplate, substitutions).trim();
    String text = substitute(this.textTemplate, substitutions);
    return title + "\n\n" + text;
  }

  private Map<String, String> buildSubstitutions(ClashHittersRecruitStats stats) {
    Map<String, String> map = new HashMap<>();
    map.put("clan_name", stringValue(stats.getClanName()));
    map.put("clan_tag", stringValue(stats.getClanTag()));
    map.put("clan_level", stringValue(stats.getClanLevel()));
    map.put("required_town_hall", stringValue(stats.getRequiredTownHall()));
    map.put("clan_war_league", stringValue(stats.getClanWarLeague()));
    map.put("capital_hall_level", stringValue(stats.getCapitalHallLevel()));
    map.put("clan_capital_league", stringValue(stats.getClanCapitalLeague()));
    map.put("clan_website", buildClanWebsite(stats.getClanName()));
    map.put("war_log", formatWarLog(stats.getWars()));
    map.put("raid_log", formatRaidLog(stats.getRaids()));
    map.put("clan_games_log", formatClanGamesLog(stats.getClanGames()));
    return map;
  }

  private String buildClanWebsite(String clanName) {
    if (!StringUtils.hasText(clanName)) {
      return "";
    }
    return "https://" + clanName.toLowerCase(Locale.ROOT) + ".clashhitters.com";
  }

  private String formatWarLog(List<ClashHittersWar> wars) {
    if (wars == null || wars.isEmpty()) {
      return EMPTY_LOG_ROW;
    }
    StringBuilder sb = new StringBuilder();
    for (ClashHittersWar war : wars) {
      sb.append("| ")
          .append(formatUtcDate(war.getEndTime()))
          .append(" | ")
          .append(stringValue(war.getTeamSize()))
          .append(" | ")
          .append(stringValue(war.getClanStars()))
          .append(" | ")
          .append(formatDestruction(war.getClanDestructionPercentage()))
          .append(" | ")
          .append(formatWarResult(war))
          .append(" |\n");
    }
    return sb.toString().trim();
  }

  private String formatRaidLog(List<ClashHittersRaid> raids) {
    if (raids == null || raids.isEmpty()) {
      return EMPTY_LOG_ROW;
    }
    StringBuilder sb = new StringBuilder();
    for (ClashHittersRaid raid : raids) {
      sb.append("| ")
          .append(formatUtcDate(raid.getStartTime()))
          .append(" | ")
          .append(formatUtcDate(raid.getEndTime()))
          .append(" | ")
          .append(stringValue(raid.getTotalAttacks()))
          .append(" | ")
          .append(formatNumber(raid.getCapitalTotalLoot()))
          .append(" | ")
          .append(formatNumber(raid.getTotalRewardIf6Attacks()))
          .append(" |\n");
    }
    return sb.toString().trim();
  }

  private String formatClanGamesLog(List<ClashHittersClanGame> clanGames) {
    if (clanGames == null || clanGames.isEmpty()) {
      return "| - | - | - | - |";
    }
    StringBuilder sb = new StringBuilder();
    for (ClashHittersClanGame game : clanGames) {
      sb.append("| ")
          .append(formatUtcDate(game.getStartDate()))
          .append(" | ")
          .append(formatUtcDate(game.getEndDate()))
          .append(" | ")
          .append(formatNumber(game.getGoal()))
          .append(" | ")
          .append(formatNumber(game.getScore()))
          .append(" |\n");
    }
    return sb.toString().trim();
  }

  private String formatWarResult(ClashHittersWar war) {
    String result = war.getResult();
    if (!StringUtils.hasText(result)) {
      return formatCwlLabel(war.getEndTime());
    }
    return switch (result.toLowerCase(Locale.ROOT)) {
      case "win" -> "Won";
      case "lose" -> "Lost";
      default -> result.substring(0, 1).toUpperCase(Locale.ROOT) + result.substring(1).toLowerCase(Locale.ROOT);
    };
  }

  private String formatCwlLabel(String endTime) {
    if (!StringUtils.hasText(endTime)) {
      return "CWL";
    }
    try {
      ZonedDateTime zoned = ZonedDateTime.parse(endTime, COC_UTC_FORMAT).withZoneSameInstant(this.zoneId);
      String monthYear =
          DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US).format(zoned);
      return monthYear + " CWL";
    } catch (DateTimeParseException e) {
      log.warn("Failed to parse CWL end time: {}", endTime, e);
      return "CWL";
    }
  }

  private String formatDestruction(Double destruction) {
    if (destruction == null) {
      return "-";
    }
    return String.format(Locale.US, "%.1f%%", destruction);
  }

  private String formatUtcDate(String utcTimestamp) {
    if (!StringUtils.hasText(utcTimestamp)) {
      return "-";
    }
    try {
      ZonedDateTime zoned = ZonedDateTime.parse(utcTimestamp, COC_UTC_FORMAT).withZoneSameInstant(this.zoneId);
      return this.displayDateFormat.format(zoned);
    } catch (DateTimeParseException e) {
      log.warn("Failed to parse timestamp: {}", utcTimestamp, e);
      return "-";
    }
  }

  private String formatNumber(Integer value) {
    if (value == null) {
      return "-";
    }
    return String.format(Locale.US, "%,d", value);
  }

  private String substitute(String template, Map<String, String> substitutions) {
    String result = template;
    for (Map.Entry<String, String> entry : substitutions.entrySet()) {
      result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
    }
    return result;
  }

  private String stringValue(Object value) {
    return value == null ? "" : Objects.toString(value);
  }

  private String readClasspathResource(String path) throws IOException {
    ClassPathResource resource = new ClassPathResource(path);
    return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
  }
}

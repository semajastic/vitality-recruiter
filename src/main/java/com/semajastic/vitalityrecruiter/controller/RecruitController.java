package com.semajastic.vitalityrecruiter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.semajastic.vitalityrecruiter.service.RecruitService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recruit")
public class RecruitController {

  @Autowired private RecruitService recruitService;

  @GetMapping(value = "/preview", produces = MediaType.TEXT_MARKDOWN_VALUE)
  public Mono<String> preview() {
    return this.recruitService.buildPreviewMarkdown();
  }
}

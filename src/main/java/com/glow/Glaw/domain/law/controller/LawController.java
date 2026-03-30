package com.glow.Glaw.domain.law.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.law.dto.LawListResponseDto;
import com.glow.Glaw.domain.law.service.LawService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/laws")
@RequiredArgsConstructor
public class LawController {
	private final LawService lawService;

	// 법률 리스트 조회
	@GetMapping
	public List<LawListResponseDto> getLawList() {
		return lawService.getLawList();
	}
}

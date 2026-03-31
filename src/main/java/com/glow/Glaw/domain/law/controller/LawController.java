package com.glow.Glaw.domain.law.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.law.dto.LawDetailResponseDto;
import com.glow.Glaw.domain.law.dto.LawListResponseDto;
import com.glow.Glaw.domain.law.service.LawService;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;
import com.glow.Glaw.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/laws")
@RequiredArgsConstructor
public class LawController {
	private final LawService lawService;

	// 법률 리스트 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<LawListResponseDto>>> getLawList(
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		Long userId = (user != null) ? user.getUserId() : null;
		List<LawListResponseDto> response = lawService.getLawList(userId);

		System.out.println("user = " + user);
		if (user == null) {
			System.out.println("user null 들어옴");
		}

		return ResponseEntity.ok(
			ApiResponse.success("법률 리스트 조회 성공", response)
		);
	}

	// 법률 상세 조회
	@GetMapping("/{lawId}")
	public ResponseEntity<ApiResponse<LawDetailResponseDto>> getLawDetail(
		@PathVariable Long lawId
	){
		LawDetailResponseDto response = lawService.getLawDetail(lawId);

		return ResponseEntity.ok(
			ApiResponse.success("법률 상세 조회 성공", response)
		);
	}

	// 법률 검색
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<LawListResponseDto>>> searchLaws(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long countryId,
		@RequestParam(required = false) String lawType
	){
		Long userId = (user != null) ? user.getUserId() : null;

		List<LawListResponseDto> response = lawService.searchLaws(userId, keyword, countryId, lawType);

		return ResponseEntity.ok(
			ApiResponse.success("법률 검색 성공", response)
		);
	}
}

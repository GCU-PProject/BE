package com.glow.Glaw.domain.law.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.law.dto.LawDetailResponseDto;
import com.glow.Glaw.domain.law.dto.LawListResponseDto;
import com.glow.Glaw.domain.law.service.LawService;
import com.glow.Glaw.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/laws")
@RequiredArgsConstructor
public class LawController {
	private final LawService lawService;

	// 법률 리스트 조회
	@GetMapping
	public ResponseEntity<ApiResponse<List<LawListResponseDto>>> getLawList() {
		List<LawListResponseDto> response = lawService.getLawList();

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
}

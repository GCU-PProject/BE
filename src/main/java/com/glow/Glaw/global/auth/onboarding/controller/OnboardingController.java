package com.glow.Glaw.global.auth.onboarding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;
import com.glow.Glaw.global.auth.onboarding.dto.request.OnboardingRequestDto;
import com.glow.Glaw.global.auth.onboarding.dto.response.OnboardingResponseDto;
import com.glow.Glaw.global.auth.onboarding.service.OnboardingService;
import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;
import com.glow.Glaw.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OnboardingController {
	private final OnboardingService onboardingService;

	@PostMapping("/onboarding")
	public ResponseEntity<ApiResponse<OnboardingResponseDto>> onboarding(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody OnboardingRequestDto onboardingRequestDto
	) {
		if (user == null) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		OnboardingResponseDto response = onboardingService.complete(
			user.getUserId(),
			onboardingRequestDto
		);

		return ResponseEntity.ok(
			ApiResponse.success("온보딩 완료", response)
		);
	}
}

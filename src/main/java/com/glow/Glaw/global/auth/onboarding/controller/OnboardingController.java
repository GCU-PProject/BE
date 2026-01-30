package com.glow.Glaw.global.auth.onboarding.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;
import com.glow.Glaw.global.auth.onboarding.dto.request.OnboardingRequestDto;
import com.glow.Glaw.global.auth.onboarding.dto.response.OnboardingResponseDto;
import com.glow.Glaw.global.auth.onboarding.service.OnboardingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OnboardingController {
	private final OnboardingService onboardingService;

	@PostMapping("/onboarding")
	public OnboardingResponseDto onboarding(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody OnboardingRequestDto onboardingRequestDto
	) {
		return onboardingService.complete(
			user.getUserId(),
			onboardingRequestDto
		);
	}
}

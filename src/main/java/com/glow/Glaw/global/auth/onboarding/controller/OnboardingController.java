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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OnboardingController {
	private final OnboardingService onboardingService;

	@PostMapping("/onboarding")
	public ResponseEntity<ApiResponse<Void>> onboarding(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody OnboardingRequestDto onboardingRequestDto,
		HttpServletResponse response
	) {
		if (user == null) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		OnboardingResponseDto tokens = onboardingService.complete(
			user.getUserId(),
			onboardingRequestDto
		);

		// AccessToken -> HttpOnly Cookie에 저장
		Cookie accessCookie = new Cookie("accessToken", tokens.getAccessToken());
		accessCookie.setHttpOnly(true);
		accessCookie.setSecure(false);
		accessCookie.setDomain(".glaw.site");
		accessCookie.setPath("/");
		accessCookie.setMaxAge(60 * 60); // 1시간
		response.addCookie(accessCookie);

		// RefreshToken -> HttpOnly Cookie에 저장
		Cookie refreshCookie = new Cookie("refreshToken", tokens.getRefreshToken());
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false);
		refreshCookie.setDomain(".glaw.site");
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
		response.addCookie(refreshCookie);

		return ResponseEntity.ok(ApiResponse.success("온보딩 완료", null));
	}
}

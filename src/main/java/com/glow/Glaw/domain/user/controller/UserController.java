package com.glow.Glaw.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.user.dto.UserMeResponseDto;
import com.glow.Glaw.domain.user.service.UserService;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/me")
	public UserMeResponseDto getMyInfo(
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		return userService.getMyInfo(user.getUserId());
	}
}
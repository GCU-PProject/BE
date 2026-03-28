package com.glow.Glaw.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.user.dto.UpdateUserCountriesRequestDto;
import com.glow.Glaw.domain.user.dto.UserMeResponseDto;
import com.glow.Glaw.domain.user.service.UserService;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;
import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;
import com.glow.Glaw.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserMeResponseDto>> getMyInfo(
		@AuthenticationPrincipal CustomOAuth2User user
	) {
		if (user == null) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		UserMeResponseDto response = userService.getMyInfo(user.getUserId());

		return ResponseEntity.ok(
			ApiResponse.success("내 정보 조회 성공", response)
		);
	}

	@PatchMapping("/countries")
	public ResponseEntity<ApiResponse<Void>> updateMyCountries(
		@AuthenticationPrincipal CustomOAuth2User user,
		@RequestBody UpdateUserCountriesRequestDto requestDto
	) {
		if (user == null) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		userService.updateMyCountries(
			user.getUserId(),
			requestDto.getCountryIds()
		);

		return ResponseEntity.ok(
			ApiResponse.success("국가 정보 수정 완료", null)
		);
	}
}
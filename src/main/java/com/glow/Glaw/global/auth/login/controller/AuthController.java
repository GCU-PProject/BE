package com.glow.Glaw.global.auth.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glow.Glaw.domain.shared.Role;
import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.domain.user.repository.UserRepository;
import com.glow.Glaw.global.auth.jwt.JwtProvider;
import com.glow.Glaw.global.auth.login.service.RefreshTokenService;
import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;
import com.glow.Glaw.global.response.ApiResponse;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;

	// 1. AccessToken 재발급 (RefreshToken으로 AccessToken 발급)
	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<Void>> reissue(HttpServletRequest request, HttpServletResponse response) {
		// 1) RefreshToken 쿠키에서 꺼내기
		String refreshToken = jwtProvider.extractRefreshCookie(request)
			.orElseThrow(() -> new CommonException(ErrorCode.JWT_TOKEN_MISSING));

		// 2) RefreshToken 유효성 검사
		if (!jwtProvider.validateToken(refreshToken)) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}

		// 3) refreshToken에서 email / userId 파싱
		Long userId = jwtProvider.getUserIdFromToken(refreshToken);
		String email = jwtProvider.getEmailFromToken(refreshToken);
		String name = jwtProvider.getNameFromToken(refreshToken);
		Role role = jwtProvider.getRoleFromToken(refreshToken);

		// 4) 유저 존재 여부 확인
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CommonException(ErrorCode.USER_NOT_FOUND));

		// 5) Redis에 저장된 RefreshToken과 일치하는지 확인
		refreshTokenService.validateStoredRefreshToken(user.getId(), refreshToken);

		// 6) 새 AccessToken 발급
		String newAccessToken = jwtProvider.createAccessToken(userId, email, name, role);

		// 7) AccessToken -> HttpOnly Cookie에 저장
		Cookie accessCookie = new Cookie("accessToken", newAccessToken);
		accessCookie.setHttpOnly(true);
		accessCookie.setSecure(false);
		accessCookie.setDomain("glaw.site");
		accessCookie.setPath("/");
		accessCookie.setMaxAge(60 * 60); // 1시간
		response.addCookie(accessCookie);

		log.info("New Access Token: {}", newAccessToken);

		// 8) 프론트에 반환
		return ResponseEntity.ok(ApiResponse.success("AccessToken 재발급 성공", null));
	}

	// 2. 로그아웃 (RefreshToken 삭제 + 쿠키 만료)
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
		// 1) 쿠키에서 refreshToken 꺼내기
		String refreshToken = jwtProvider.extractRefreshCookie(request).orElse(null);

		if (refreshToken != null) {
			// 2) refreshToken에서 userId 파싱
			Long userId = jwtProvider.getUserIdFromToken(refreshToken);

			// 3) Redis에서 RefreshToken 삭제
			refreshTokenService.deleteRefreshToken(userId);
		}

		// 4) 쿠키 삭제 (AccessToken + RefreshToken)
		expireCookie(response, "accessToken");
		expireCookie(response, "refreshToken");

		log.info("로그아웃 완료");

		return ResponseEntity.ok(ApiResponse.success("로그아웃 성공", null));
	}

	// 쿠키 삭제
	private void expireCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setPath("/");
		cookie.setDomain("glaw.site");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}

package com.glow.Glaw.global.auth.login.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.glow.Glaw.global.auth.jwt.JwtProvider;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// AccessToken / RefreshToken 발급 후 accesstoken은 헤더로, refreshtoken은 쿠키로 프론트에 전달
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	private final JwtProvider jwtProvider;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {
		// 1) SecurityContext에서 로그인 정보 수신
		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		Long userId = oAuth2User.getUserId();
		String email = oAuth2User.getEmail();
		String name = oAuth2User.getName();

		log.info("Login Success, userId={}, email={}", userId, email);

		// 2) AccessToken, RefreshToken 생성
		String accessToken = jwtProvider.createAccessToken(userId, email, name);
		String refreshToken = jwtProvider.createRefreshToken(userId, email, name);

		// 3) RefreshToken 저장
		jwtProvider.storeRefreshToken(userId, refreshToken);

		// 4) AccessToken -> Response Header 전달
		response.addHeader("Authorization", "Bearer " + accessToken);

		// 5) RefreshToken -> HttpOnly Cookie에 저장
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
		response.addCookie(refreshCookie);

		// 6) 프론트로 최종 리다이렉트
		response.sendRedirect("http://localhost:3000/");
		log.info("AccessToken: {}", accessToken);
	}
}

package com.glow.Glaw.global.auth.login.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.glow.Glaw.domain.shared.Role;
import com.glow.Glaw.global.auth.jwt.JwtProvider;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// AccessToken / RefreshToken 발급 후 HttpOnly 쿠키로 프론트에 전달
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
		Role role = oAuth2User.getRole();

		log.info("Login Success, userId={}, email={}, role={}", userId, email, role);

		// 2) AccessToken, RefreshToken 생성
		String accessToken = jwtProvider.createAccessToken(userId, email, name, role);
		String refreshToken = jwtProvider.createRefreshToken(userId, email, name, role);

		// 3) RefreshToken 저장
		jwtProvider.storeRefreshToken(userId, refreshToken);

		// 4) AccessToken -> HttpOnly Cookie에 저장
		Cookie accessCookie = new Cookie("accessToken", accessToken);
		accessCookie.setHttpOnly(true);
		accessCookie.setSecure(false);
		accessCookie.setDomain("glaw.site");
		accessCookie.setPath("/");
		accessCookie.setMaxAge(60 * 60); // 1시간
		response.addCookie(accessCookie);

		// 5) RefreshToken -> HttpOnly Cookie에 저장
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(false);
		refreshCookie.setDomain("glaw.site");
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
		response.addCookie(refreshCookie);

		// 6) 프론트로 최종 리다이렉트
		// 사용자 권한에 따라 redirect URL 설정
		if (role == Role.ROLE_GUEST) {
			response.sendRedirect("http://glaw.site/onboarding");
		} else {
			response.sendRedirect("http://glaw.site");
		}

		log.info("AccessToken: {}", accessToken);
	}
}

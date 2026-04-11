package com.glow.Glaw.global.auth.login.handler;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.glow.Glaw.domain.shared.Role;
import com.glow.Glaw.global.auth.jwt.JwtProvider;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// AccessToken / RefreshToken 발급 후 HttpOnly 쿠키로 프론트에 전달
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	private final JwtProvider jwtProvider;

	@Value("${cookie.domain:localhost}")
	private String cookieDomain;

	@Value("${cookie.redirect-url:http://localhost:3000}")
	private String defaultRedirectUrl;

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

		// 3) RefreshToken Redis에 저장
		jwtProvider.storeRefreshToken(userId, refreshToken);

		// 4) 리다이렉트 URL 결정 (세션에서 가져오거나 기본값 사용)
		String redirectUrl = getRedirectUrl(request, role);

		// 5) 쿠키 도메인 결정
		String domain = determineCookieDomain(redirectUrl);
		boolean isLocalhost = domain.equals("localhost");

		// 6) AccessToken -> HttpOnly Cookie에 저장
		Cookie accessCookie = new Cookie("accessToken", accessToken);
		accessCookie.setHttpOnly(true);
		accessCookie.setSecure(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(60 * 60); // 1시간
		if (isLocalhost) {
			accessCookie.setAttribute("SameSite", "None");
		}
		if (!isLocalhost) {
			accessCookie.setDomain(domain);
		}
		response.addCookie(accessCookie);

		// 7) RefreshToken -> HttpOnly Cookie에 저장
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
		if (isLocalhost) {
			refreshCookie.setAttribute("SameSite", "None");
		}
		if (!isLocalhost) {
			refreshCookie.setDomain(domain);
		}
		response.addCookie(refreshCookie);

		// 8) 프론트로 최종 리다이렉트
		response.sendRedirect(redirectUrl);
		log.info("Redirecting to: {}", redirectUrl);
	}

	private String getRedirectUrl(HttpServletRequest request, Role role) {
		HttpSession session = request.getSession(false);
		String customRedirectUri = null;

		if (session != null) {
			customRedirectUri = (String) session.getAttribute("CUSTOM_REDIRECT_URI");
			session.removeAttribute("CUSTOM_REDIRECT_URI");
		}

		// 프론트에서 보낸 redirect_uri가 있고, 화이트리스트에 포함되면 사용
		if (customRedirectUri != null && !customRedirectUri.isEmpty() && isValidRedirectUri(customRedirectUri)) {
			return customRedirectUri;
		}

		// 없거나 허용되지 않은 URL이면 기본값 + role에 따라 경로 결정
		if (role == Role.ROLE_GUEST) {
			return defaultRedirectUrl + "/onboarding";
		}
		return defaultRedirectUrl;
	}

	// 화이트리스트 검증
	private boolean isValidRedirectUri(String uri) {
		return uri.startsWith("http://localhost:3000")
			|| uri.startsWith("http://localhost:5173")
			|| uri.startsWith("http://glaw.site")
			|| uri.startsWith("https://glaw.site")
			|| uri.startsWith("http://www.glaw.site")
			|| uri.startsWith("https://www.glaw.site");
	}

	private String determineCookieDomain(String redirectUrl) {
		try {
			URI uri = new URI(redirectUrl);
			String host = uri.getHost();

			if (host == null || host.equals("localhost")) {
				return "localhost";
			}

			// glaw.site 도메인
			if (host.endsWith("glaw.site")) {
				return "glaw.site";
			}

			return host;
		} catch (Exception e) {
			return cookieDomain;
		}
	}
}

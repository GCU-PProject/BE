package com.glow.Glaw.global.auth.jwt;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.glow.Glaw.domain.shared.Role;
import com.glow.Glaw.global.auth.login.domain.CustomOAuth2User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// JWT가 유효한지 검증 담당
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtProvider jwtProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		// 1) 토큰 추출 (쿠키 우선, 없으면 헤더)
		String token = extractToken(request);

		if (token == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 2) 토큰 검증
		if (!jwtProvider.validateToken(token)) {
			log.warn("Invalid JWT Token");
			filterChain.doFilter(request, response);
			return;
		}

		// 3) 토큰에서 userId, email 추출
		Long userId = jwtProvider.getUserIdFromToken(token);
		String email = jwtProvider.getEmailFromToken(token);
		String name = jwtProvider.getNameFromToken(token);
		Role role = jwtProvider.getRoleFromToken(token);

		// SecurityContext에 넣을 CustomOAuth2User 생성
		CustomOAuth2User customUser = new CustomOAuth2User(
			userId,
			email,
			name,
			role,
			null
		);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(
				customUser,
				null,
				customUser.getAuthorities()
			);

		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		// 4) SecurityContext에 저장
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}

	// 토큰 추출 (쿠키 우선, 없으면 Authorization 헤더)
	private String extractToken(HttpServletRequest request) {
		// 1) 쿠키에서 추출 시도
		String token = extractTokenFromCookie(request);
		if (token != null) {
			return token;
		}

		// 2) Authorization 헤더에서 추출 (Swagger 테스트용)
		String authorization = request.getHeader("Authorization");
		if (authorization != null && authorization.startsWith("Bearer ")) {
			return authorization.substring(7);
		}

		return null;
	}

	private String extractTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		return Arrays.stream(cookies)
			.filter(cookie -> "accessToken".equals(cookie.getName()))
			.map(Cookie::getValue)
			.findFirst()
			.orElse(null);
	}
}

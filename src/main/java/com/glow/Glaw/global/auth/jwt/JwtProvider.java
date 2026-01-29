package com.glow.Glaw.global.auth.jwt;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// AccessToken / RefreshToken 생성 / RefreshToken Redis 저장 / JWT 검증 / JWT에서 userId, email 추출
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.access.expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh.expiration}")
	private long refreshTokenExpiration;

	private final StringRedisTemplate redisTemplate;

	private Key key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	// 1. Access Token 생성
	public String createAccessToken(Long userId, String email, String name) {
		return createToken(userId, email, name, accessTokenExpiration);
	}

	// 2. Refresh Token 생성
	public String createRefreshToken(Long userId, String email, String name) {
		return createToken(userId, email, name, refreshTokenExpiration);
	}

	// 3. JWT 생성
	private String createToken(Long userId, String email, String name, long expireTimeMs) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + expireTimeMs);

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("email", email)
			.claim("name", name)
			.setIssuedAt(now)
			.setExpiration(expiry)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// 4. RefreshToken 저장
	public void storeRefreshToken(Long userId, String refreshToken) {
		redisTemplate.opsForValue().set(
			"RT:" + userId,
			refreshToken,
			refreshTokenExpiration,
			TimeUnit.MILLISECONDS
		);
	}

	// 5. JWT 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);

			return true;
		} catch (ExpiredJwtException e) {
			log.warn("JWT Expired: {}", e.getMessage());
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("Invalid JWT: {}", e.getMessage());
		}
		return false;
	}

	// 6. JWT에서 userId 꺼내기
	public Long getUserIdFromToken(String token) {
		Claims claims = getAllClaims(token);
		return Long.valueOf(claims.getSubject());
	}

	// 7. JWT에서 email 꺼내기
	public String getEmailFromToken(String token) {
		Claims claims = getAllClaims(token);
		return claims.get("email", String.class);
	}

	// 8. JWT에서 name 꺼내기
	public String getNameFromToken(String token) {
		Claims claims = getAllClaims(token);
		return claims.get("name", String.class);
	}

	private Claims getAllClaims(String token) {
		return Jwts.parser()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	// 9. 쿠키에서 accessToken 추출
	public Optional<String> extractAccessCookie(HttpServletRequest request) {
		if (request.getCookies() != null) return Optional.empty();

		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("access-token")) {
				return Optional.of(cookie.getValue());
			}
		}

		return Optional.empty();
	}

	// 10. 쿠키에서 refreshToken 추출
	public Optional<String> extractRefreshCookie(HttpServletRequest request) {
		if (request.getCookies() == null) return Optional.empty();

		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("refresh-token")) {
				return Optional.of(cookie.getValue());
			}
		}

		return Optional.empty();
	}
}

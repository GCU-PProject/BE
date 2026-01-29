package com.glow.Glaw.global.auth.login.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

// 토큰 검증 & 로그아웃
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
	private final StringRedisTemplate redisTemplate;

	// Redis에 저장된 Refresh Token 가져오기
	public String getStoredRefreshToken(Long userId) {
		return redisTemplate.opsForValue().get("user:" + userId);
	}

	// Redis에 저장된 Refresh Token과 일치하는지 확인
	public void validateStoredRefreshToken(Long userId, String refreshToken) {
		String storedToken = getStoredRefreshToken(userId);

		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new RuntimeException("Refresh Token이 일차하지 않습니다.");
		}
	}

	// Redis에 Refresh Token 삭제 (로그아웃)
	public void deleteRefreshToken(Long userId) {
		redisTemplate.delete("user:" + userId);
	}
}

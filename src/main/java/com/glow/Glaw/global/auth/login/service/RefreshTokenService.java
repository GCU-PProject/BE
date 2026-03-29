package com.glow.Glaw.global.auth.login.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 토큰 검증 & 로그아웃
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
	private final StringRedisTemplate redisTemplate;

	// Redis에 저장된 Refresh Token 가져오기
	public String getStoredRefreshToken(Long userId) {
		return redisTemplate.opsForValue().get("RT:" + userId);
	}

	// Redis에 저장된 Refresh Token과 일치하는지 확인
	public void validateStoredRefreshToken(Long userId, String refreshToken) {
		String storedToken = getStoredRefreshToken(userId);

		log.info("stroedToken: {}", storedToken);
		log.info("refreshToken: {}", refreshToken);

		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new CommonException(ErrorCode.JWT_TOKEN_INVALID);
		}
	}

	// Redis에 Refresh Token 삭제 (로그아웃)
	public void deleteRefreshToken(Long userId) {
		redisTemplate.delete("RT:" + userId);
	}
}

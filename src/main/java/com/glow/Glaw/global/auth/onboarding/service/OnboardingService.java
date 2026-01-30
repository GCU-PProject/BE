package com.glow.Glaw.global.auth.onboarding.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glow.Glaw.domain.country.domain.Country;
import com.glow.Glaw.domain.country.repository.CountryRepository;
import com.glow.Glaw.domain.shared.Role;
import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.domain.user.domain.UserCountry;
import com.glow.Glaw.domain.user.repository.UserCountryRepository;
import com.glow.Glaw.domain.user.repository.UserRepository;
import com.glow.Glaw.global.auth.jwt.JwtProvider;
import com.glow.Glaw.global.auth.onboarding.dto.request.OnboardingRequestDto;
import com.glow.Glaw.global.auth.onboarding.dto.response.OnboardingResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {
	private final UserRepository userRepository;
	private final CountryRepository countryRepository;
	private final UserCountryRepository userCountryRepository;
	private final JwtProvider jwtProvider;

	public OnboardingResponseDto complete(Long userId, OnboardingRequestDto onboardingRequestDto) {
		// 1) 유저 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

		// 2) 이미 온보딩 완료된 경우 차단
		if (user.getRole() != Role.ROLE_GUEST) {
			throw new IllegalArgumentException("온보딩이 이미 완료되었습니다.");
		}

		List<Long> countryIds = onboardingRequestDto.getCountryIds();
		if (countryIds == null || countryIds.isEmpty()) {
			throw new IllegalArgumentException("국가를 한 개 이상 선택해야 합니다.");
		}

		// 3) UserCountry 생성
		for (Long countryId : countryIds) {
			Country country = countryRepository.findById(countryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가입니다."));

			// 중복 방지
			if (userCountryRepository.existsByUser_IdAndCountry_Id(userId, countryId)) {
				continue;
			}

			userCountryRepository.save(
				UserCountry.create(user, country)
			);
		}

		// 4) Role 변경 (회원하입 완료)
		user.changeRole(Role.ROLE_USER);

		// 5) 새 JWT 발급
		String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
		String newRefreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail(), user.getName(), user.getRole());

		return new OnboardingResponseDto(newAccessToken, newRefreshToken);
	}
}

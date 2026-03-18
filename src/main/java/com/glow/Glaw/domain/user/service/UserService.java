package com.glow.Glaw.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glow.Glaw.domain.country.domain.Country;
import com.glow.Glaw.domain.country.repository.CountryRepository;
import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.domain.user.domain.UserCountry;
import com.glow.Glaw.domain.user.dto.UserMeResponseDto;
import com.glow.Glaw.domain.user.repository.UserCountryRepository;
import com.glow.Glaw.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final UserCountryRepository userCountryRepository;
	private final CountryRepository countryRepository;

	// 유저 정보 조회
	public UserMeResponseDto getMyInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("정보 조회 실패: 유저가 존재하지 않습니다."));

		List<UserCountry> userCountries = userCountryRepository.findAllByUser_Id(userId);

		List<UserMeResponseDto.CountryDto> countries = userCountries.stream()
			.map(uc -> new UserMeResponseDto.CountryDto(
				uc.getCountry().getId(),
				uc.getCountry().getCountryCode(),
				uc.getCountry().getCountryName()
			))
			.toList();

		return new UserMeResponseDto(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getRole(),
			countries
		);
	}

	// 관심 나라 수정
	public void updateMyCountries(Long userId, List<Long> countryIds) {

		if (countryIds == null || countryIds.isEmpty()) {
			throw new IllegalArgumentException("관심 국가는 최소 1개 이상이어야 합니다.");
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

		// 1) 기존 관심 국가 전부 삭제
		userCountryRepository.deleteAllByUser_Id(userId);
		userCountryRepository.flush();

		// 2) 새로 등록
		for (Long countryId : countryIds) {
			Country country = countryRepository.findById(countryId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가입니다."));

			userCountryRepository.save(
				UserCountry.create(user, country)
			);
		}
	}
}

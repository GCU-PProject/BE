package com.glow.Glaw.domain.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.glow.Glaw.domain.user.domain.User;
import com.glow.Glaw.domain.user.domain.UserCountry;
import com.glow.Glaw.domain.user.dto.UserMeResponseDto;
import com.glow.Glaw.domain.user.repository.UserCountryRepository;
import com.glow.Glaw.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final UserCountryRepository userCountryRepository;

	public UserMeResponseDto getMyInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("정보 조회 실패: 유저가 존재하지 않습니다."));

		List<UserCountry> userCountries = userCountryRepository.findAllByUser_Id(userId);

		List<UserMeResponseDto.CountryDto> countries = userCountries.stream()
			.map(uc -> new UserMeResponseDto.CountryDto(
				uc.getCountry().getId(),
				uc.getCountry().getCode(),
				uc.getCountry().getName()
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
}

package com.glow.Glaw.domain.user.dto;

import java.util.List;

import com.glow.Glaw.domain.shared.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMeResponseDto {
	private Long userId;
	private String email;
	private String name;
	private Role role;
	private List<CountryDto> countries;

	@Getter
	@AllArgsConstructor
	public static class CountryDto {
		private Long countryId;
		private String code;
		private String name;
	}
}

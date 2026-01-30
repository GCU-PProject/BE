package com.glow.Glaw.domain.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class UpdateUserCountriesRequestDto {
	private List<Long> countryIds;
}

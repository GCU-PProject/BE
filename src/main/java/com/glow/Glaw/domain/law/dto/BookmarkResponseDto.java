package com.glow.Glaw.domain.law.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkResponseDto {
	private Long lawId;
	private String countryName;
	private String lawTitle;
}

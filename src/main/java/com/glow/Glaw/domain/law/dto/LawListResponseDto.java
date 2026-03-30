package com.glow.Glaw.domain.law.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LawListResponseDto {
	private Long lawId;
	private String countryName;
	private String lawTitle;
	private String summary;
	private LocalDateTime updatedAt;
}

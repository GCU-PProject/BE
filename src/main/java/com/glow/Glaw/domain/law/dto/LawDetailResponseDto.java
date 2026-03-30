package com.glow.Glaw.domain.law.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LawDetailResponseDto {
	private Long lawId;
	private String countryName;
	private String lawType;
	private String title;
	private String sourceUrl;
	private LocalDateTime updatedAt;
	private List<ArticleDto> articles;

	@Getter
	@AllArgsConstructor
	public static class ArticleDto {
		private String articleNo;
		private String title;
		private String content;
	}
}

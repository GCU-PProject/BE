package com.glow.Glaw.domain.law.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RawDetailResponseDto {
	private Long lawId;
	private String countryName;
	private String lawType;
	private String title;
	private String sourceUrl;
	private LocalDateTime updatedAt;
	private List<ArticleDto> articles;

	@Getter
	@Builder
	public static class ArticleDto {
		private String articleNo;
		private String title;
		private String content;
	}
}

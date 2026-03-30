package com.glow.Glaw.domain.law.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.glow.Glaw.domain.law.domain.Law;
import com.glow.Glaw.domain.law.dto.LawListResponseDto;
import com.glow.Glaw.domain.law.repository.LawRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LawService {
	private final LawRepository lawRepository;

	public List<LawListResponseDto> getLawList() {
		List<Law> laws = lawRepository.findAllWithCountry();

		return laws.stream()
			.map(law -> LawListResponseDto.builder()
				.lawId(law.getId())
				.countryName(law.getCountry().getCountryName())
				.lawTitle(getTitle(law))
				.summary(makeSummary(law.getContent()))
				.updatedAt(law.getUpdatedAt())
				.build()
			)
			.toList();
	}

	// 제목 안전 처리
	private String getTitle(Law law) {
		return law.getSectionTitle() != null
			? law.getSectionTitle()
			: "제목 없음";
	}

	// 요약 생성
	private String makeSummary(String content) {
		if (content == null) return "";

		return content.length() > 100
			? content.substring(0, 100) + ". . ."
			: content;
	}
}

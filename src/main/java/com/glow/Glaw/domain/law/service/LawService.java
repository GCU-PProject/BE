package com.glow.Glaw.domain.law.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.glow.Glaw.domain.law.domain.Law;
import com.glow.Glaw.domain.law.dto.LawDetailResponseDto;
import com.glow.Glaw.domain.law.dto.LawListResponseDto;
import com.glow.Glaw.domain.law.repository.LawRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LawService {
	private final LawRepository lawRepository;

	// 법률 리스트 조회
	public List<LawListResponseDto> getLawList() {
		List<Law> laws = lawRepository.findAllWithCountry();

		return laws.stream()
			.map(law -> new LawListResponseDto(
					law.getId(),
					law.getCountry().getCountryName(),
					law.getSectionTitle() != null ? law.getSectionTitle() : "제목 없음",
					law.getContent() != null && law.getContent().length() > 100
						? law.getContent().substring(0, 100) + ". . ."
						: law.getContent(),
					law.getUpdatedAt()
			))
			.toList();
	}

	// 법률 상세 조회
	public LawDetailResponseDto getLawDetail(Long lawId) {
		List<Law> laws = lawRepository.findAllBySameLaw(lawId);

		if (laws.isEmpty()) {
			throw new IllegalArgumentException("정보 조회 실패: 법률이 존재하지 않습니다.");
		}

		Law first = laws.get(0);

		List<LawDetailResponseDto.ArticleDto> articles = laws.stream()
			.map(law -> new LawDetailResponseDto.ArticleDto(
				law.getArticleNo(),
				law.getSectionTitle(),
				law.getContent()
			))
			.toList();

		return new LawDetailResponseDto(
			first.getId(),
			first.getCountry().getCountryName(),
			first.getLawType(),
			first.getSectionTitle(),
			first.getSourceUrl(),
			first.getUpdatedAt(),
			articles
		);
	}
}

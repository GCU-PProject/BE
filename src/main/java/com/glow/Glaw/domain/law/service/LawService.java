package com.glow.Glaw.domain.law.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.glow.Glaw.domain.law.domain.Law;
import com.glow.Glaw.domain.law.dto.LawDetailResponseDto;
import com.glow.Glaw.domain.law.dto.LawListResponseDto;
import com.glow.Glaw.domain.law.repository.LawRepository;
import com.glow.Glaw.domain.law.repository.UserLawRepository;
import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.error.exception.CommonException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LawService {
	private final LawRepository lawRepository;
	private final UserLawRepository userLawRepository;

	// 법률 리스트 조회
	public List<LawListResponseDto> getLawList(Long userId) {
		List<Law> laws = lawRepository.findAllWithCountry();

		// userId null 방어 (비로그인)
		Set<Long> bookmarkedLawIds = (userId != null)
			? userLawRepository.findAllByUserId(userId).stream()
			.map(ul -> ul.getLaw().getId())
			.collect(Collectors.toSet())
			: Set.of();

		return laws.stream()
			.map(law -> new LawListResponseDto(
					law.getId(),
					law.getCountry().getCountryName(),
					law.getSectionTitle() != null ? law.getSectionTitle() : "제목 없음",
					law.getContent() != null && law.getContent().length() > 100
						? law.getContent().substring(0, 100) + ". . ."
						: law.getContent(),
					law.getUpdatedAt(),
					bookmarkedLawIds.contains(law.getId())
			))
			.toList();
	}

	// 법률 상세 조회
	public LawDetailResponseDto getLawDetail(Long lawId) {
		List<Law> laws = lawRepository.findAllBySameLaw(lawId);

		if (laws.isEmpty()) {
			throw new CommonException(ErrorCode.LAW_NOT_FOUND);
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

	// 법률 검색
	public List<LawListResponseDto> searchLaws(Long userId, String keyword, Long countryId, String lawType) {
		List<Law> laws = lawRepository.searchLaws(keyword, countryId, lawType);

		// 북마크 set
		Set<Long> bookmarkedLawIds = (userId != null)
			? userLawRepository.findAllByUserId(userId).stream()
			.map(ul -> ul.getLaw().getId())
			.collect(Collectors.toSet())
			: Set.of();

		return laws.stream()
			.map(law -> new LawListResponseDto(
					law.getId(),
					law.getCountry().getCountryName(),
					law.getSectionTitle() != null ? law.getSectionTitle() : "제목 없음",
					law.getContent() != null && law.getContent().length() > 100
						? law.getContent().substring(0, 100) + ". . ."
						: law.getContent(),
					law.getUpdatedAt(),
					bookmarkedLawIds.contains(law.getId())
			))
			.toList();
	}
}

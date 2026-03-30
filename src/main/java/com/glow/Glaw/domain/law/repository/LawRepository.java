package com.glow.Glaw.domain.law.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.glow.Glaw.domain.law.domain.Law;

public interface LawRepository extends JpaRepository<Law, Long> {
	// N+1 방지 + 정렬
	@Query("SELECT l FROM Law l JOIN FETCH l.country ORDER BY l.updatedAt DESC")
	List<Law> findAllWithCountry();

	@Query("""
		SELECT l FROM Law l
		JOIN FETCH l.country
		WHERE l.sectionTitle = (
			SELECT l2.sectionTitle FROM Law l2 WHERE l2.id = :lawId
		)
		ORDER BY l.articleNo
	""")
	List<Law> findAllBySameLaw(Long lawId);

	@Query("""
		SELECT l FROM Law l
		JOIN FETCH l.country
		WHERE 
			(:keyword IS NULL OR l.content LIKE %:keyword% OR l.sectionTitle LIKE %:keyword%)
		AND (:countryId IS NULL OR l.country.id = :countryId)
		AND (:lawType IS NULL OR l.lawType = :lawType)
		ORDER BY l.updatedAt DESC
	""")
	List<Law> searchLaws(
		String keyword,
		Long countryId,
		String lawType
	);
}

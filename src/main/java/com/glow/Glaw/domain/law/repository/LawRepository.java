package com.glow.Glaw.domain.law.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.glow.Glaw.domain.law.domain.Law;

public interface LawRepository extends JpaRepository<Law, Long> {
	// N+1 방지 + 정렬
	@Query("SELECT l FROM Law l JOIN FETCH l.country ORDER BY l.updatedAt DESC")
	List<Law> findAllWithCountry();
}

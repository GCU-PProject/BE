package com.glow.Glaw.domain.law.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.glow.Glaw.domain.law.domain.UserLaw;

public interface UserLawRepository extends JpaRepository<UserLaw, Long> {
	Optional<UserLaw> findByUser_IdAndLaw_Id(Long userId, Long lawId);
	void deleteByUser_IdAndLaw_Id(Long userId, Long lawId);
	boolean existsByUser_IdAndLaw_Id(Long userId, Long lawId);

	@Query("""
		SELECT ul FROM UserLaw ul
		JOIN FETCH ul.law l
		JOIN FETCH l.country
		WHERE ul.user.id = :userId
		ORDER BY ul.createdAt DESC
	""")
	List<UserLaw> findAllByUserId(Long userId);
}

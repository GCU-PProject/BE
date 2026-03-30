package com.glow.Glaw.domain.law.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glow.Glaw.domain.law.domain.UserLaw;

public interface UserLawRepository extends JpaRepository<UserLaw, Long> {
	Optional<UserLaw> findByUser_IdAndLaw_Id(Long userId, Long lawId);
	void deleteByUser_IdAndLaw_Id(Long userId, Long lawId);
	boolean existsByUser_IdAndLaw_Id(Long userId, Long lawId);
}

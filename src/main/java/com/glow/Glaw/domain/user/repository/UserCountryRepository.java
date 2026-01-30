package com.glow.Glaw.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glow.Glaw.domain.user.domain.UserCountry;

public interface UserCountryRepository extends JpaRepository<UserCountry, Long> {
	// 온보딩 여부 확인
	boolean existsByUser_Id(Long userId);

	// 유저의 관심 국가 전체 조회
	List<UserCountry> findAllByUser_Id(Long userId);

	// 특정 유저 + 국가 존재 여부 (중복 방지용)
	boolean existsByUser_IdAndCountry_Id(Long userId, Long countryId);
}


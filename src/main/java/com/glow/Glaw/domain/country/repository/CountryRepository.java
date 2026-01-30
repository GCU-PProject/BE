package com.glow.Glaw.domain.country.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glow.Glaw.domain.country.domain.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}

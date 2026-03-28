package com.glow.Glaw.domain.country.domain;

import com.glow.Glaw.domain.shared.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "countries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "country_id")
	private Long id;

	@Column(name = "country_code", length = 10, nullable = false)
	private String countryCode;

	@Column(name = "country_name", length = 100, nullable = false)
	private String countryName;

	@Column(name = "state_code", length = 10, nullable = true)
	private String stateCode;

	@Column(name = "state_name", length = 100, nullable = true)
	private String stateName;
}

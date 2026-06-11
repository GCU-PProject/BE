package com.glow.Glaw.domain.law.domain;

import java.time.LocalDateTime;

import com.glow.Glaw.domain.country.domain.Country;
import com.glow.Glaw.domain.shared.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "laws")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Law extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "law_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_id", nullable = false)
	private Country country;

	@Column(name = "law_type", length = 20, nullable = false)
	private String lawType;

	@Column(name = "section_title", columnDefinition = "TEXT")
	private String sectionTitle;

	@Column(name = "article_no", nullable = false, columnDefinition = "TEXT")
	private String articleNo;

	@Column(name = "content", columnDefinition = "TEXT", nullable = false)
	private String content;

	@Column(name = "source_url", columnDefinition = "TEXT")
	private String sourceUrl;

	@Column(name = "enactment_date")
	private LocalDateTime enactmentDate;

	@Column(name = "amendment_date")
	private LocalDateTime amendmentDate;
}

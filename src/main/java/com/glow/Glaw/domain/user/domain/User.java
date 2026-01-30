package com.glow.Glaw.domain.user.domain;

import com.glow.Glaw.domain.shared.BaseTimeEntity;
import com.glow.Glaw.domain.shared.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	private Role role;

	// Builder를 통해서만 객체 생성
	@Builder
	private User(String email, String name) {
		this.email = email;
		this.name = name;
		this.role = Role.ROLE_GUEST;
	}

	// 사용자 생성 Method
	public static User createUser(String email, String name) {
		return User.builder()
			.email(email)
			.name(name)
			.build();
	}
}

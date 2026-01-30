package com.glow.Glaw.global.auth.login.domain;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.glow.Glaw.domain.shared.Role;

import lombok.Getter;

// entity를 SecurityContext에 저장할 포맷 만들기
@Getter
public class CustomOAuth2User implements OAuth2User {
	private final Long userId;
	private final String email;
	private final String name;
	private final Role role;
	private final Map<String, Object> attributes;

	public CustomOAuth2User(Long userId, String email, String name, Role role, Map<String, Object> attributes) {
		this.userId = userId;
		this.email = email;
		this.name = name;
		this.role = role;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	// Security 권한 검사를 위한 Role 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	// 주요 식별 키 (userId)
	@Override
	public String getName() {
		return userId.toString();
	}
}

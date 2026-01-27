package com.glow.Glaw.global.auth.login.domain;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class CustomOAuth2User implements OAuth2User {
	private final Long userId;
	private final String email;
	private final String name;
	private final Map<String, Object> attributes;

	public CustomOAuth2User(Long userId, String email, String name, Map<String, Object> attributes) {
		this.userId = userId;
		this.email = email;
		this.name = name;
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	// role 관련 (권한 기능 사용하지 않지만 OAuth2User 인터페이스에서 필요로 함) -> 권한 없으므로 null로 반환
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	// 주요 식별 키 (email)
	@Override
	public String getName() {
		return userId.toString();
	}
}

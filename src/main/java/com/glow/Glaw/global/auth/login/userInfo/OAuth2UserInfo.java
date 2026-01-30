package com.glow.Glaw.global.auth.login.userInfo;

import java.util.Map;

import com.glow.Glaw.domain.shared.Role;

public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public abstract String getEmail();
	public abstract String getName();
}

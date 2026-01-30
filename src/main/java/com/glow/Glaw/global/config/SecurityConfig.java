package com.glow.Glaw.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.glow.Glaw.global.auth.jwt.JwtAuthenticationFilter;
import com.glow.Glaw.global.auth.login.handler.OAuth2LoginSuccessHandler;
import com.glow.Glaw.global.auth.login.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

// spring security 전체 동작
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// 1) JWT 기반 인증 -> 세션 사용 없음
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)

			// 2) URL 권한 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/oauth2/**",
					"/api/auth/**"
				).permitAll()
				.requestMatchers(
					"/swagger-ui/**",
					"/v3/api-docs/**"
				).permitAll()
				.anyRequest().authenticated()
			)

			// 3) OAuth2 Login 설정
			.oauth2Login(oauth -> oauth
				.userInfoEndpoint(userInfo ->
					userInfo.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2LoginSuccessHandler) // JWT 발급 후 redirect
			)

			// 4) 폼 로그인 / 기본 로그인 비활성화
			.formLogin(form -> form.disable())
			.httpBasic(httpBasic -> httpBasic.disable());

		// JWT 필터 추가 (UsernamePasswordAuthenticationFilter 앞에) -> 매 요청마다 인증 처리
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

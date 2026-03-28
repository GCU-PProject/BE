package com.glow.Glaw.global.auth.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json;charset=UTF-8");

		// 온보딩 API에 ROLE_USER가 접근한 경우
		if (request.getRequestURI().contains("/onboarding")) {
			response.getWriter().write(
				ApiResponse.fail(ErrorCode.ONBOARDING_ALREADY_COMPLETED).toJson()
			);
		} else {
			response.getWriter().write(
				ApiResponse.fail(ErrorCode.JWT_ACCESS_DENIED).toJson()
			);
		}
	}
}

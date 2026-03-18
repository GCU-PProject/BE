package com.glow.Glaw.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//공통
	VALIDATION_FAILED(400, "VALIDATION_FAILED", "요청 값이 올바르지 않습니다."),
	JSON_PARSING_ERROR(400, "JSON_PARSING_ERROR", "JSON 데이터 처리 중 오류가 발생했습니다"),
	INVALID_PARAMETER_TYPE		(400, "INVALID_PARAMETER_TYPE", "적절하지 않은 파라미터 타입입니다."),
	VALIDATION_ERROR(400, "VALIDATION_ERROR", "유효성 검사 오류입니다."),
	INVALID_REQUEST_FORMAT(400, "INVALID_REQUEST_FORMAT", "올바르지 않은 요청 형식입니다."),
	NO_RESOURCE_FOUND(404, "NO_RESOURCE_FOUND", "해당 리소스를 찾을 수 없습니다."),
	UNSUPPORTED_MEDIA_TYPE(415, "UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 미디어 타입입니다."),
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 에러"),

	// JWT 토큰 관련 에러
	JWT_INVALID_FORMAT(401, "JWT_INVALID_FORMAT", "올바르지 않은 토큰 형식입니다"),
	JWT_TOKEN_EXPIRED(401, "JWT_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
	JWT_TOKEN_MISSING(401, "JWT_TOKEN_MISSING", "토큰이 없습니다."),
	JWT_TOKEN_INVALID(401, "JWT_TOKEN_INVALID", "유효하지 않은 토큰입니다."),
	JWT_TOKEN_BLACKLISTED(401, "JWT_TOKEN_BLACKLISTED", "블랙리스트에 등록된 토큰입니다"),
	JWT_ACCESS_DENIED(403, "JWT_ACCESS_DENIED", "유효하지 않은 토큰입니다.(권한 부족)"),

	//회원
	INVALID_LOGIN_INFO(401, "INVALID_LOGIN_INFO", "로그인 정보가 올바르지 않습니다."),
	INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
	USER_NOT_FOUND(404, "USER_NOT_FOUND", "유저를 찾을 수 없습니다."),
	TOKEN_NOT_FOUND(404, "TOKEN_NOT_FOUND", "토큰를 찾을 수 없습니다."),
	MEMBER_ALREADY_EXISTS(409, "MEMBER_ALREADY_EXISTS", "이미 가입된 이메일입니다."),
	DUPLICATE_PHONE_NUMBER(409, "DUPLICATE_PHONE_NUMBER", "이미 사용 중인 전화번호입니다.");

	private final int status;
	private final String code;
	private final String message;
}


package com.glow.Glaw.global.error.exception;

import com.glow.Glaw.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {
	private final ErrorCode errorCode;

	public CommonException(ErrorCode errorCode) {
		super(errorCode.getMessage()); // 👈 메시지 전달 추가! 토큰 에러응답을 위해서
		this.errorCode = errorCode;
	}
}

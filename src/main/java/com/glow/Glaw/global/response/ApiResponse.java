package com.glow.Glaw.global.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.glow.Glaw.global.error.ErrorCode;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {
	private boolean success;
	private int status;
	private String code;
	private String message;
	private LocalDateTime timestamp;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> error;

	@Builder
	public ApiResponse(boolean success, int status, String code, String message, LocalDateTime timestamp, T result,
		Map<String, String> error) {
		this.success = success;
		this.status = status;
		this.code = code;
		this.message = message;
		this.timestamp = timestamp;
		this.result = result;
		this.error = error;
	}

	// 201 Create 성공 응답
	public static <T> ApiResponse<T> success(T result, int status) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(true)
			.status(status)
			.code("SUCCESS")
			.message("성공")
			.timestamp(now)
			.result(result)
			.error(null)
			.build();
	}

	// 메시지를 받는 성공 응답 메소드 추가
	public static <T> ApiResponse<T> success(String message, T result) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(true)
			.status(200)
			.code("SUCCESS")
			.message(message)  // 커스텀 메시지 사용
			.timestamp(now)
			.result(result)
			.error(null)
			.build();
	}

	// 성공 응답 200
	public static <T> ApiResponse<T> success(T result) {
		return success(result, 200);
	}

	//성공 응답 result 없는 버전
	public static <T> ApiResponse<T> success() {
		return success(null, 200);
	}

	//실패 응답 (단일 필드)
	public static <T> ApiResponse<T> fail(ErrorCode code, String field, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(field, message);
		return fail(code, errorMap);
	}

	// 실패 응답 (필드 별로)
	public static <T> ApiResponse<T> fail(ErrorCode code, Map<String, String> errorMap) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(false)
			.status(code.getStatus())
			.code(code.getCode())
			.message(code.getMessage())
			.timestamp(now)
			.result(null)
			.error(errorMap)
			.build();
	}

	// 실패 응답 (에러 내용 있는 경우)
	public static <T> ApiResponse<T> fail(ErrorCode code, String message) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(false)
			.status(code.getStatus())
			.code(code.getCode())
			.message(message)
			.timestamp(now)
			.result(null)
			.error(null)
			.build();
	}

	// 실패 응답 (에러 내용 없는 경우)
	public static <T> ApiResponse<T> fail(ErrorCode code) {
		LocalDateTime now = LocalDateTime.now();
		return ApiResponse.<T>builder()
			.success(false)
			.status(code.getStatus())
			.code(code.getCode())
			.message(code.getMessage())
			.timestamp(now)
			.result(null)
			.error(null)
			.build();
	}

	public String toJson() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		return mapper.writeValueAsString(this);
	}
}

package com.glow.Glaw.global.error.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.glow.Glaw.global.error.ErrorCode;
import com.glow.Glaw.global.response.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private static void showErrorLog(ErrorCode errorCode) {
		log.error("errorCode: {}, message: {}", errorCode.getCode(), errorCode.getMessage());
	}

	// 에러 메세지 생성 메서드
	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode, Map<String, String> errors) {
		//ErrorResponse errorResponse = ErrorResponse.of(errorCode); //기능 확장시 사용할 것
		return ResponseEntity
			.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(ApiResponse.fail(errorCode, errors));
	}

	// 단일 에러 처리
	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
		return ResponseEntity
			.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(ApiResponse.fail(errorCode));
	}

	// 단일 에러 처리
	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode, String message) {
		return ResponseEntity
			.status(HttpStatus.valueOf(errorCode.getStatus()))
			.body(ApiResponse.fail(errorCode, message));
	}

	// 커스텀 처리되지 않은 보통 에러 처리 (500)
	@ExceptionHandler(Exception.class)
	public  ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

		log.error(ex.getMessage());
		return buildErrorResponse(errorCode, errorCode.getMessage());
	}

	// 커스텀 처리된 에러 처리(해당 코드)
	@ExceptionHandler(CommonException.class) // Custom Exception을 포괄적으로 처리
	public ResponseEntity<ApiResponse<Void>> handleCommonException(CommonException ex) {
		ErrorCode errorCode = ex.getErrorCode(); // 전달된 예외에서 에러 코드 가져오기

		showErrorLog(errorCode);
		return buildErrorResponse(errorCode, errorCode.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.collect(Collectors.toMap(
				fieldError -> fieldError.getField(),
				fieldError -> fieldError.getDefaultMessage(),
				(oldVal, newVal) -> newVal // 중복 필드 처리
			));
		return buildErrorResponse(ErrorCode.VALIDATION_FAILED, errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
		Map<String, String> errors = ex.getConstraintViolations()
			.stream()
			.collect(Collectors.toMap(
				v -> v.getPropertyPath().toString(),
				ConstraintViolation::getMessage,
				(oldVal, newVal) -> newVal
			));
		return buildErrorResponse(ErrorCode.VALIDATION_FAILED, errors);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<ApiResponse<Void>> handleTransactionSystemException(TransactionSystemException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof ConstraintViolationException) {
			return handleConstraintViolationException((ConstraintViolationException)cause);
		}
		return buildErrorResponse(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException ex) {
		String errorMessage = String.format("파라미터 '%s' 가 적절하지 않은 값을 가지고 있습니다: %s", ex.getName(), ex.getValue());
		return buildErrorResponse(ErrorCode.INVALID_PARAMETER_TYPE, errorMessage);
	}

	@ExceptionHandler(ConversionFailedException.class)
	public ResponseEntity<ApiResponse<Void>> handleConversionFailedException(ConversionFailedException ex) {
		String errorMessage = String.format("ENUM '%s'에 '%s' 값이 존재하지 않습니다.",
			ex.getTargetType().getType().getSimpleName(), ex.getValue());
		return buildErrorResponse(ErrorCode.INVALID_PARAMETER_TYPE, errorMessage);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException ex) {
		String errorMessage = String.format("필수 파라미터 '%s'가 누락되었습니다.", ex.getParameterName());
		return buildErrorResponse(ErrorCode.INVALID_PARAMETER_TYPE, errorMessage);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		return buildErrorResponse(ErrorCode.VALIDATION_ERROR, "필수 입력 필드가 누락되었습니다.");
	}

	// @ExceptionHandler(HttpMessageConversionException.class)
	// public ResponseEntity<ApiResponse<Void>> handleHttpMessageConversionException(HttpMessageConversionException ex) {
	// 	String errorMessage = "잘못된 요청 형식입니다. multipart/form-data 형식으로 요청해주세요.";
	//
	// 	if (ex.getCause() instanceof InvalidDefinitionException && ex.getMessage().contains("MultipartFile")) {
	// 		errorMessage = "파일 업로드는 multipart/form-data 형식으로 요청해주세요.";
	// 	}
	// 	return buildErrorResponse(ErrorCode.INVALID_REQUEST_FORMAT, errorMessage);
	// }
	//
	// @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	// public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(
	// 	HttpMediaTypeNotSupportedException ex) {
	// 	return buildErrorResponse(ErrorCode.UNSUPPORTED_MEDIA_TYPE,
	// 		"지원하지 않는 Content-Type입니다. multipart/form-data 형식으로 요청해주세요.");
	// }
}

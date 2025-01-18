package kr.hhplus.be.server.interfaces.api.common.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.domain.common.exception.UnprocessableEntityException;
import kr.hhplus.be.server.domain.queuetoken.exception.InvalidToken;
import kr.hhplus.be.server.interfaces.api.common.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = InvalidToken.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidToken(InvalidToken e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(value= UnprocessableEntityException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleUnprocessableEntityException(UnprocessableEntityException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":400,\"message\":\"BAD_REQUEST error message\"}")))
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(BindException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getBindingResult());
    }

    @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":400,\"message\":\"BAD_REQUEST error message\"}")))
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":400,\"message\":\"BAD_REQUEST error message\"}")))
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":400,\"message\":\"BAD_REQUEST error message\"}")))
    @ExceptionHandler(value = MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestValueException(MissingRequestValueException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "BAD_REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":400,\"message\":\"Parameter Type Error\"}")))
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        String message = "[" + paramName + "] 파라미터의 형식이 올바르지 않습니다. " + requiredType + " 타입으로 입력해주세요.";
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class, example = "{\"statusCode\":500,\"message\":\"에러가 발생했습니다.\"}")))
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error(Arrays.toString(e.getStackTrace()));
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "에러가 발생했습니다.");
    }
}

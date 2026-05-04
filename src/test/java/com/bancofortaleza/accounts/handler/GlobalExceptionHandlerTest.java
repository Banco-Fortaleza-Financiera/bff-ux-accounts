package com.bancofortaleza.accounts.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bancofortaleza.accounts.domain.exceptions.ApiException;
import com.bancofortaleza.accounts.domain.model.ErrorResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleFeignExceptionShouldPreserveValidStatusBodyAndAllowedHeaders() {
        FeignException exception = feignException(
            404,
            Map.of(
                "x-correlation-id", List.of("abc"),
                HttpHeaders.CONNECTION, List.of("close")
            ),
            "{\"code\":\"NOT_FOUND\"}"
        );

        ResponseEntity<byte[]> result = handler.handleFeignException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getHeaders()).containsEntry("x-correlation-id", List.of("abc"));
        assertThat(result.getHeaders()).doesNotContainKey(HttpHeaders.CONNECTION);
        assertThat(new String(result.getBody(), StandardCharsets.UTF_8)).isEqualTo("{\"code\":\"NOT_FOUND\"}");
    }

    @Test
    void handleFeignExceptionShouldReturnBadGatewayForInvalidStatus() {
        FeignException exception = feignException(0, Map.of(), "");

        ResponseEntity<byte[]> result = handler.handleFeignException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(result.getBody()).isNull();
    }

    @Test
    void handleFeignExceptionShouldReturnBadGatewayWhenStatusIsAboveHttpRange() {
        FeignException exception = feignException(600, Map.of(), "");

        ResponseEntity<byte[]> result = handler.handleFeignException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void handleFeignExceptionShouldIgnoreNullHeaderNamesAndValues() {
        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put(null, List.of("ignored"));
        headers.put("x-null-values", null);
        headers.put("x-valid", List.of("kept"));
        FeignException exception = mock(FeignException.class);
        when(exception.status()).thenReturn(400);
        when(exception.responseHeaders()).thenReturn(headers);
        when(exception.content()).thenReturn("bad-request".getBytes(StandardCharsets.UTF_8));

        ResponseEntity<byte[]> result = handler.handleFeignException(exception);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getHeaders()).containsEntry("x-valid", List.of("kept"));
        assertThat(result.getHeaders()).doesNotContainKey("x-null-values");
    }

    @Test
    void handleApiExceptionShouldBuildErrorResponse() {
        HttpServletRequest request = request("/accounts");
        ApiException exception = new ApiException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "No tiene autorizacion");

        ResponseEntity<ErrorResponse> result = handler.handleApiException(exception, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().code()).isEqualTo("UNAUTHORIZED");
        assertThat(result.getBody().message()).isEqualTo("No tiene autorizacion");
        assertThat(result.getBody().path()).isEqualTo("/accounts");
        assertThat(result.getBody().details()).isEmpty();
    }

    @Test
    void handleValidationShouldReturnFieldErrorsWithPublicHeaderNames() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
            new FieldError("request", "xDeviceIp", "must not be blank"),
            new FieldError("request", "body.name", "must not be blank"),
            new FieldError("request", "body.", "must not end with a dot")
        ));

        ResponseEntity<ErrorResponse> result = handler.handleValidation(exception, request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.getBody().details())
            .extracting(ErrorResponse.FieldError::field)
            .containsExactly("x-device-ip", "name", "body.");
    }

    @Test
    void handleConstraintViolationShouldReturnSanitizedFieldErrors() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("list.xSession");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must match pattern");
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ErrorResponse> result = handler.handleConstraintViolation(exception, request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().details()).containsExactly(
            new ErrorResponse.FieldError("x-session", "must match pattern")
        );
    }

    @Test
    void handleBadRequestShouldReturnInvalidRequestResponse() {
        ResponseEntity<ErrorResponse> result =
            handler.handleBadRequest(mock(HttpMessageNotReadableException.class), request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(result.getBody().message()).isEqualTo("Invalid request");
    }

    @Test
    void handleMissingRequestParameterShouldIncludeParameterDetail() {
        MissingServletRequestParameterException exception =
            new MissingServletRequestParameterException("x-page", "Integer");

        ResponseEntity<ErrorResponse> result = handler.handleMissingRequestParameter(exception, request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("MISSING_REQUIRED_PARAMETER");
        assertThat(result.getBody().details()).containsExactly(
            new ErrorResponse.FieldError("x-page", "Parameter is required")
        );
    }

    @Test
    void handleMissingRequestHeaderShouldIncludeHeaderDetail() {
        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);
        when(exception.getHeaderName()).thenReturn("x-session");

        ResponseEntity<ErrorResponse> result = handler.handleMissingRequestHeader(exception, request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().code()).isEqualTo("MISSING_REQUIRED_HEADER");
        assertThat(result.getBody().details()).containsExactly(
            new ErrorResponse.FieldError("x-session", "Header is required")
        );
    }

    @Test
    void handleNotFoundShouldReturnNotFoundResponse() {
        ResponseEntity<ErrorResponse> result = handler.handleNotFound(new RuntimeException("missing"), request("/missing"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().code()).isEqualTo("NOT_FOUND");
        assertThat(result.getBody().path()).isEqualTo("/missing");
    }

    @Test
    void handleMethodNotAllowedShouldReturnMethodNotAllowedResponse() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ErrorResponse> result = handler.handleMethodNotAllowed(exception, request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(result.getBody().code()).isEqualTo("METHOD_NOT_ALLOWED");
    }

    @Test
    void handleUnexpectedShouldReturnInternalServerErrorResponse() {
        ResponseEntity<ErrorResponse> result = handler.handleUnexpected(new RuntimeException("boom"), request("/accounts"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody().code()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(result.getBody().message()).isEqualTo("Unexpected error");
    }

    private HttpServletRequest request(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

    private FeignException feignException(int status, Map<String, Collection<String>> headers, String body) {
        Request request = Request.create(
            Request.HttpMethod.GET,
            "/support/v1/accounts",
            Map.of(),
            null,
            StandardCharsets.UTF_8,
            null
        );
        Response response = Response.builder()
            .request(request)
            .status(status)
            .reason("status")
            .headers(headers)
            .body(body, StandardCharsets.UTF_8)
            .build();
        return FeignException.errorStatus("SupportApiClient#listAccounts", response);
    }
}

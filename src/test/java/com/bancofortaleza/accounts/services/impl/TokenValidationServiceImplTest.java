package com.bancofortaleza.accounts.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.bancofortaleza.accounts.domain.exceptions.ApiException;
import com.bff.services.auth.AuthApiClient;
import com.bff.services.auth.models.TokenValidationRequest;
import com.bff.services.auth.models.TokenValidationResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TokenValidationServiceImplTest {

    private static final String DEVICE_IP = "192.168.1.10";
    private static final String SESSION = "7f2c1a54-4cf0-4d8c-9b18-2a6a57e7f7f3";

    @Mock
    private AuthApiClient authApiClient;

    @InjectMocks
    private TokenValidationServiceImpl service;

    @Test
    void validateShouldReturnUserIdWhenTokenIsValid() {
        TokenValidationResponse validationResponse = new TokenValidationResponse()
            .valid(true)
            .idUser(25);
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenReturn(ResponseEntity.ok(validationResponse));

        Integer userId = service.validate("Bearer access-token", DEVICE_IP, SESSION);

        assertThat(userId).isEqualTo(25);
        ArgumentCaptor<TokenValidationRequest> requestCaptor = ArgumentCaptor.forClass(TokenValidationRequest.class);
        verify(authApiClient).validateToken(eq(DEVICE_IP), eq(SESSION), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getAccessToken()).isEqualTo("access-token");
        assertThat(requestCaptor.getValue().getTokenType()).isEqualTo("Bearer");
    }

    @Test
    void validateShouldAcceptBearerHeaderIgnoringCaseAndExtraSpaces() {
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenReturn(ResponseEntity.ok(new TokenValidationResponse().valid(true).idUser(7)));

        Integer userId = service.validate("  bearer   spaced-token  ", DEVICE_IP, SESSION);

        assertThat(userId).isEqualTo(7);
        ArgumentCaptor<TokenValidationRequest> requestCaptor = ArgumentCaptor.forClass(TokenValidationRequest.class);
        verify(authApiClient).validateToken(eq(DEVICE_IP), eq(SESSION), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getAccessToken()).isEqualTo("spaced-token");
    }

    @Test
    void validateShouldThrowUnauthorizedWhenAuthorizationHeaderIsBlank() {
        assertThatThrownBy(() -> service.validate(" ", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> {
                ApiException apiException = (ApiException) exception;
                assertThat(apiException.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
                assertThat(apiException.getCode()).isEqualTo("UNAUTHORIZED");
            });

        verifyNoInteractions(authApiClient);
    }

    @Test
    void validateShouldThrowUnauthorizedWhenAuthorizationHeaderIsNull() {
        assertThatThrownBy(() -> service.validate(null, DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> assertThat(((ApiException) exception).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));

        verifyNoInteractions(authApiClient);
    }

    @Test
    void validateShouldThrowUnauthorizedWhenBearerTokenIsMissing() {
        assertThatThrownBy(() -> service.validate("Bearer", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> assertThat(((ApiException) exception).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));

        verifyNoInteractions(authApiClient);
    }

    @Test
    void validateShouldThrowUnauthorizedWhenAuthorizationHeaderIsNotBearer() {
        assertThatThrownBy(() -> service.validate("Basic token", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .hasMessage("No tiene autorizacion");

        verifyNoInteractions(authApiClient);
    }

    @Test
    void validateShouldThrowUnauthorizedWhenResponseBodyIsNull() {
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenReturn(ResponseEntity.ok(null));

        assertThatThrownBy(() -> service.validate("Bearer token", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> assertThat(((ApiException) exception).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void validateShouldThrowUnauthorizedWhenTokenIsInvalid() {
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenReturn(ResponseEntity.ok(new TokenValidationResponse().valid(false).idUser(10)));

        assertThatThrownBy(() -> service.validate("Bearer token", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> assertThat(((ApiException) exception).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void validateShouldThrowUnauthorizedWhenUserIdIsNull() {
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenReturn(ResponseEntity.ok(new TokenValidationResponse().valid(true)));

        assertThatThrownBy(() -> service.validate("Bearer token", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> assertThat(((ApiException) exception).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void validateShouldThrowUnauthorizedWhenAuthServiceReturnsUnauthorized() {
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenThrow(feignException(401));

        assertThatThrownBy(() -> service.validate("Bearer token", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> assertThat(((ApiException) exception).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void validateShouldThrowServiceUnavailableWhenAuthServiceFails() {
        when(authApiClient.validateToken(eq(DEVICE_IP), eq(SESSION), org.mockito.ArgumentMatchers.any()))
            .thenThrow(feignException(503));

        assertThatThrownBy(() -> service.validate("Bearer token", DEVICE_IP, SESSION))
            .isInstanceOf(ApiException.class)
            .satisfies(exception -> {
                ApiException apiException = (ApiException) exception;
                assertThat(apiException.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                assertThat(apiException.getCode()).isEqualTo("TOKEN_VALIDATION_UNAVAILABLE");
            });
    }

    private FeignException feignException(int status) {
        Request request = Request.create(
            Request.HttpMethod.POST,
            "/auth/validate-token",
            Map.of(),
            null,
            StandardCharsets.UTF_8,
            null
        );
        Response response = Response.builder()
            .request(request)
            .status(status)
            .reason("status")
            .body("{}", StandardCharsets.UTF_8)
            .build();
        return FeignException.errorStatus("AuthApiClient#validateToken", response);
    }
}

package com.bancofortaleza.accounts.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.accounts.configuration.SupportHeadersProvider;
import com.bancofortaleza.accounts.services.mapper.OpenApiModelMapper;
import com.bff.services.client.SupportApiClient;
import com.bff.services.server.models.AccountCreateRequest;
import com.bff.services.server.models.AccountResponse;
import com.bff.services.server.models.AccountStatusUpdateRequest;
import com.bff.services.server.models.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final String DEVICE_IP = "192.168.1.10";
    private static final String SESSION = "7f2c1a54-4cf0-4d8c-9b18-2a6a57e7f7f3";
    private static final Integer USER_ID = 99;

    @Mock
    private SupportApiClient supportApiClient;

    @Mock
    private OpenApiModelMapper mapper;

    @Mock
    private SupportHeadersProvider supportHeadersProvider;

    @InjectMocks
    private AccountServiceImpl service;

    @Test
    void listAccountsShouldCallClientWithAuthenticatedUserAndMappedStatus() {
        com.bff.services.client.models.Status clientStatus = com.bff.services.client.models.Status.ACTIVE;
        ResponseEntity<List<com.bff.services.client.models.AccountResponse>> clientResponse =
            ResponseEntity.ok(List.of(new com.bff.services.client.models.AccountResponse().id(1)));
        ResponseEntity<List<AccountResponse>> expectedResponse =
            ResponseEntity.ok(List.of(new AccountResponse().id(1)));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(mapper.map(Status.ACTIVE, com.bff.services.client.models.Status.class)).thenReturn(clientStatus);
        when(supportApiClient.listAccounts(DEVICE_IP, SESSION, USER_ID, 1, 20, "sav", 10, 2, clientStatus))
            .thenReturn(clientResponse);
        when(mapper.mapListResponse(clientResponse, AccountResponse.class)).thenReturn(expectedResponse);

        ResponseEntity<List<AccountResponse>> result =
            service.listAccounts(DEVICE_IP, SESSION, 1, 20, "sav", 10, 2, Status.ACTIVE);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).listAccounts(DEVICE_IP, SESSION, USER_ID, 1, 20, "sav", 10, 2, clientStatus);
    }

    @Test
    void createAccountShouldMapRequestAndResponse() {
        AccountCreateRequest request = new AccountCreateRequest();
        com.bff.services.client.models.AccountCreateRequest clientRequest =
            new com.bff.services.client.models.AccountCreateRequest();
        ResponseEntity<com.bff.services.client.models.AccountResponse> clientResponse =
            ResponseEntity.status(HttpStatus.CREATED).body(new com.bff.services.client.models.AccountResponse().id(5));
        ResponseEntity<AccountResponse> expectedResponse =
            ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse().id(5));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(mapper.map(request, com.bff.services.client.models.AccountCreateRequest.class)).thenReturn(clientRequest);
        when(supportApiClient.createAccount(DEVICE_IP, SESSION, USER_ID, clientRequest)).thenReturn(clientResponse);
        when(mapper.mapResponse(clientResponse, AccountResponse.class)).thenReturn(expectedResponse);

        ResponseEntity<AccountResponse> result = service.createAccount(DEVICE_IP, SESSION, request);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).createAccount(DEVICE_IP, SESSION, USER_ID, clientRequest);
    }

    @Test
    void getAccountByIdShouldDelegateToClient() {
        ResponseEntity<com.bff.services.client.models.AccountResponse> clientResponse =
            ResponseEntity.ok(new com.bff.services.client.models.AccountResponse().id(8));
        ResponseEntity<AccountResponse> expectedResponse = ResponseEntity.ok(new AccountResponse().id(8));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(supportApiClient.getAccountById(DEVICE_IP, SESSION, USER_ID, 8)).thenReturn(clientResponse);
        when(mapper.mapResponse(clientResponse, AccountResponse.class)).thenReturn(expectedResponse);

        ResponseEntity<AccountResponse> result = service.getAccountById(DEVICE_IP, SESSION, 8);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).getAccountById(DEVICE_IP, SESSION, USER_ID, 8);
    }

    @Test
    void updateAccountStatusShouldMapRequestAndDelegateToClient() {
        AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
        com.bff.services.client.models.AccountStatusUpdateRequest clientRequest =
            new com.bff.services.client.models.AccountStatusUpdateRequest();
        ResponseEntity<com.bff.services.client.models.AccountResponse> clientResponse =
            ResponseEntity.ok(new com.bff.services.client.models.AccountResponse().id(12));
        ResponseEntity<AccountResponse> expectedResponse = ResponseEntity.ok(new AccountResponse().id(12));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(mapper.map(request, com.bff.services.client.models.AccountStatusUpdateRequest.class)).thenReturn(clientRequest);
        when(supportApiClient.updateAccountStatus(DEVICE_IP, SESSION, USER_ID, 12, clientRequest)).thenReturn(clientResponse);
        when(mapper.mapResponse(clientResponse, AccountResponse.class)).thenReturn(expectedResponse);

        ResponseEntity<AccountResponse> result = service.updateAccountStatus(DEVICE_IP, SESSION, 12, request);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).updateAccountStatus(DEVICE_IP, SESSION, USER_ID, 12, clientRequest);
    }

    @Test
    void getAccountByNumberShouldDelegateToClient() {
        ResponseEntity<com.bff.services.client.models.AccountResponse> clientResponse =
            ResponseEntity.ok(new com.bff.services.client.models.AccountResponse().accountNumber("123"));
        ResponseEntity<AccountResponse> expectedResponse = ResponseEntity.ok(new AccountResponse().accountNumber("123"));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(supportApiClient.getAccountByNumber(DEVICE_IP, SESSION, USER_ID, "123")).thenReturn(clientResponse);
        when(mapper.mapResponse(clientResponse, AccountResponse.class)).thenReturn(expectedResponse);

        ResponseEntity<AccountResponse> result = service.getAccountByNumber(DEVICE_IP, SESSION, "123");

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).getAccountByNumber(DEVICE_IP, SESSION, USER_ID, "123");
    }
}

package com.bancofortaleza.accounts.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.accounts.configuration.SupportHeadersProvider;
import com.bancofortaleza.accounts.services.mapper.OpenApiModelMapper;
import com.bff.services.client.SupportApiClient;
import com.bff.services.server.models.AccountTypeCreateRequest;
import com.bff.services.server.models.AccountTypeResponse;
import com.bff.services.server.models.AccountTypeStatusUpdateRequest;
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
class AccountTypeImplTest {

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
    private accountTypeImpl service;

    @Test
    void listAccountTypesShouldCallClientWithAuthenticatedUserAndMappedStatus() {
        com.bff.services.client.models.Status clientStatus = com.bff.services.client.models.Status.INACTIVE;
        ResponseEntity<List<com.bff.services.client.models.AccountTypeResponse>> clientResponse =
            ResponseEntity.ok(List.of(new com.bff.services.client.models.AccountTypeResponse().id(1)));
        ResponseEntity<List<AccountTypeResponse>> expectedResponse =
            ResponseEntity.ok(List.of(new AccountTypeResponse().id(1)));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(mapper.toClientStatus(Status.INACTIVE)).thenReturn(clientStatus);
        when(supportApiClient.listAccountTypes(DEVICE_IP, SESSION, USER_ID, 1, 20, "sav", clientStatus))
            .thenReturn(clientResponse);
        when(mapper.mapAccountTypeListResponse(clientResponse)).thenReturn(expectedResponse);

        ResponseEntity<List<AccountTypeResponse>> result =
            service.listAccountTypes(DEVICE_IP, SESSION, 1, 20, "sav", Status.INACTIVE);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).listAccountTypes(DEVICE_IP, SESSION, USER_ID, 1, 20, "sav", clientStatus);
    }

    @Test
    void createAccountTypeShouldMapRequestAndResponse() {
        AccountTypeCreateRequest request = new AccountTypeCreateRequest();
        com.bff.services.client.models.AccountTypeCreateRequest clientRequest =
            new com.bff.services.client.models.AccountTypeCreateRequest();
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> clientResponse =
            ResponseEntity.status(HttpStatus.CREATED).body(new com.bff.services.client.models.AccountTypeResponse().id(5));
        ResponseEntity<AccountTypeResponse> expectedResponse =
            ResponseEntity.status(HttpStatus.CREATED).body(new AccountTypeResponse().id(5));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(mapper.toClientAccountTypeCreateRequest(request)).thenReturn(clientRequest);
        when(supportApiClient.createAccountType(DEVICE_IP, SESSION, USER_ID, clientRequest)).thenReturn(clientResponse);
        when(mapper.mapAccountTypeResponse(clientResponse)).thenReturn(expectedResponse);

        ResponseEntity<AccountTypeResponse> result = service.createAccountType(DEVICE_IP, SESSION, request);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).createAccountType(DEVICE_IP, SESSION, USER_ID, clientRequest);
    }

    @Test
    void getAccountTypeByIdShouldDelegateToClient() {
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> clientResponse =
            ResponseEntity.ok(new com.bff.services.client.models.AccountTypeResponse().id(8));
        ResponseEntity<AccountTypeResponse> expectedResponse = ResponseEntity.ok(new AccountTypeResponse().id(8));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(supportApiClient.getAccountTypeById(DEVICE_IP, SESSION, USER_ID, 8)).thenReturn(clientResponse);
        when(mapper.mapAccountTypeResponse(clientResponse)).thenReturn(expectedResponse);

        ResponseEntity<AccountTypeResponse> result = service.getAccountTypeById(DEVICE_IP, SESSION, 8);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).getAccountTypeById(DEVICE_IP, SESSION, USER_ID, 8);
    }

    @Test
    void updateAccountTypeStatusShouldMapRequestAndDelegateToClient() {
        AccountTypeStatusUpdateRequest request = new AccountTypeStatusUpdateRequest();
        com.bff.services.client.models.AccountTypeStatusUpdateRequest clientRequest =
            new com.bff.services.client.models.AccountTypeStatusUpdateRequest();
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> clientResponse =
            ResponseEntity.ok(new com.bff.services.client.models.AccountTypeResponse().id(12));
        ResponseEntity<AccountTypeResponse> expectedResponse = ResponseEntity.ok(new AccountTypeResponse().id(12));
        when(supportHeadersProvider.getAuthenticatedUserId()).thenReturn(USER_ID);
        when(mapper.toClientAccountTypeStatusUpdateRequest(request)).thenReturn(clientRequest);
        when(supportApiClient.updateAccountTypeStatus(DEVICE_IP, SESSION, USER_ID, 12, clientRequest))
            .thenReturn(clientResponse);
        when(mapper.mapAccountTypeResponse(clientResponse)).thenReturn(expectedResponse);

        ResponseEntity<AccountTypeResponse> result = service.updateAccountTypeStatus(DEVICE_IP, SESSION, 12, request);

        assertThat(result).isSameAs(expectedResponse);
        verify(supportApiClient).updateAccountTypeStatus(DEVICE_IP, SESSION, USER_ID, 12, clientRequest);
    }
}

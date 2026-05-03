package com.bancofortaleza.accounts.services.impl;

import com.bancofortaleza.accounts.configuration.SupportHeadersProvider;
import com.bancofortaleza.accounts.services.AccountTypeService;
import com.bancofortaleza.accounts.services.mapper.OpenApiModelMapper;
import com.bff.services.client.SupportApiClient;
import com.bff.services.server.models.AccountTypeCreateRequest;
import com.bff.services.server.models.AccountTypeResponse;
import com.bff.services.server.models.AccountTypeStatusUpdateRequest;
import com.bff.services.server.models.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class accountTypeImpl implements AccountTypeService {

    private final SupportApiClient supportApiClient;
    private final OpenApiModelMapper mapper;
    private final SupportHeadersProvider supportHeadersProvider;

    @Override
    public ResponseEntity<List<AccountTypeResponse>> listAccountTypes(
        String xDeviceIp,
        String xSession,
        Integer xPage,
        Integer xPageSize,
        String search,
        Status status
    ) {
        ResponseEntity<List<com.bff.services.client.models.AccountTypeResponse>> response =
            supportApiClient.listAccountTypes(
                xDeviceIp,
                xSession,
                supportHeadersProvider.getAuthenticatedUserId(),
                xPage,
                xPageSize,
                search,
                mapper.map(status, com.bff.services.client.models.Status.class)
            );
        return mapper.mapListResponse(response, AccountTypeResponse.class);
    }

    @Override
    public ResponseEntity<AccountTypeResponse> createAccountType(
        String xDeviceIp,
        String xSession,
        AccountTypeCreateRequest accountTypeCreateRequest
    ) {
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> response =
            supportApiClient.createAccountType(
                xDeviceIp,
                xSession,
                supportHeadersProvider.getAuthenticatedUserId(),
                mapper.map(accountTypeCreateRequest, com.bff.services.client.models.AccountTypeCreateRequest.class)
            );
        return mapper.mapResponse(response, AccountTypeResponse.class);
    }

    @Override
    public ResponseEntity<AccountTypeResponse> getAccountTypeById(String xDeviceIp, String xSession, Integer id) {
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> response =
            supportApiClient.getAccountTypeById(
                xDeviceIp,
                xSession,
                supportHeadersProvider.getAuthenticatedUserId(),
                id
            );
        return mapper.mapResponse(response, AccountTypeResponse.class);
    }

    @Override
    public ResponseEntity<AccountTypeResponse> updateAccountTypeStatus(
        String xDeviceIp,
        String xSession,
        Integer id,
        AccountTypeStatusUpdateRequest accountTypeStatusUpdateRequest
    ) {
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> response =
            supportApiClient.updateAccountTypeStatus(
                xDeviceIp,
                xSession,
                supportHeadersProvider.getAuthenticatedUserId(),
                id,
                mapper.map(
                    accountTypeStatusUpdateRequest,
                    com.bff.services.client.models.AccountTypeStatusUpdateRequest.class
                )
            );
        return mapper.mapResponse(response, AccountTypeResponse.class);
    }
}

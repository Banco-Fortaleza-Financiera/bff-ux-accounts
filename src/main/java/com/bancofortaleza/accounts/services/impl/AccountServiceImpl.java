package com.bancofortaleza.accounts.services.impl;

import com.bancofortaleza.accounts.configuration.SupportHeadersProvider;
import com.bancofortaleza.accounts.services.AccountService;
import com.bancofortaleza.accounts.services.mapper.OpenApiModelMapper;
import com.bff.services.client.SupportApiClient;
import com.bff.services.server.models.AccountCreateRequest;
import com.bff.services.server.models.AccountResponse;
import com.bff.services.server.models.AccountStatusUpdateRequest;
import com.bff.services.server.models.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final SupportApiClient supportApiClient;
    private final OpenApiModelMapper mapper;
    private final SupportHeadersProvider supportHeadersProvider;

    @Override
    public ResponseEntity<List<AccountResponse>> listAccounts(
        String xDeviceIp,
        String xSession,
        Integer xPage,
        Integer xPageSize,
        String search,
        Integer idUser,
        Integer idAccountType,
        Status status
    ) {
        ResponseEntity<List<com.bff.services.client.models.AccountResponse>> response = supportApiClient.listAccounts(
            xDeviceIp,
            xSession,
            supportHeadersProvider.getAuthenticatedUserId(),
            xPage,
            xPageSize,
            search,
            idUser,
            idAccountType,
            mapper.toClientStatus(status)
        );
        return mapper.mapAccountListResponse(response);
    }

    @Override
    public ResponseEntity<AccountResponse> createAccount(
        String xDeviceIp,
        String xSession,
        AccountCreateRequest accountCreateRequest
    ) {
        ResponseEntity<com.bff.services.client.models.AccountResponse> response = supportApiClient.createAccount(
            xDeviceIp,
            xSession,
            supportHeadersProvider.getAuthenticatedUserId(),
            mapper.toClientAccountCreateRequest(accountCreateRequest)
        );
        return mapper.mapAccountResponse(response);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountById(String xDeviceIp, String xSession, Integer id) {
        ResponseEntity<com.bff.services.client.models.AccountResponse> response = supportApiClient.getAccountById(
            xDeviceIp,
            xSession,
            supportHeadersProvider.getAuthenticatedUserId(),
            id
        );
        return mapper.mapAccountResponse(response);
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccountStatus(
        String xDeviceIp,
        String xSession,
        Integer id,
        AccountStatusUpdateRequest accountStatusUpdateRequest
    ) {
        ResponseEntity<com.bff.services.client.models.AccountResponse> response = supportApiClient.updateAccountStatus(
            xDeviceIp,
            xSession,
            supportHeadersProvider.getAuthenticatedUserId(),
            id,
            mapper.toClientAccountStatusUpdateRequest(accountStatusUpdateRequest)
        );
        return mapper.mapAccountResponse(response);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountByNumber(String xDeviceIp, String xSession, String accountNumber) {
        ResponseEntity<com.bff.services.client.models.AccountResponse> response = supportApiClient.getAccountByNumber(
            xDeviceIp,
            xSession,
            supportHeadersProvider.getAuthenticatedUserId(),
            accountNumber
        );
        return mapper.mapAccountResponse(response);
    }
}

package com.bancofortaleza.accounts.services;

import com.bff.services.server.models.AccountCreateRequest;
import com.bff.services.server.models.AccountResponse;
import com.bff.services.server.models.AccountStatusUpdateRequest;
import com.bff.services.server.models.Status;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface AccountService {

    ResponseEntity<List<AccountResponse>> listAccounts(
        String xDeviceIp,
        String xSession,
        Integer xPage,
        Integer xPageSize,
        String search,
        Integer idUser,
        Integer idAccountType,
        Status status
    );

    ResponseEntity<AccountResponse> createAccount(
        String xDeviceIp,
        String xSession,
        AccountCreateRequest accountCreateRequest
    );

    ResponseEntity<AccountResponse> getAccountById(String xDeviceIp, String xSession, Integer id);

    ResponseEntity<AccountResponse> updateAccountStatus(
        String xDeviceIp,
        String xSession,
        Integer id,
        AccountStatusUpdateRequest accountStatusUpdateRequest
    );

    ResponseEntity<AccountResponse> getAccountByNumber(String xDeviceIp, String xSession, String accountNumber);
}

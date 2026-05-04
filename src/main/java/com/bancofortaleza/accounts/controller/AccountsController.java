package com.bancofortaleza.accounts.controller;

import com.bancofortaleza.accounts.services.AccountService;
import com.bancofortaleza.accounts.services.AccountTypeService;
import com.bff.services.server.ChannelApi;
import com.bff.services.server.models.AccountCreateRequest;
import com.bff.services.server.models.AccountResponse;
import com.bff.services.server.models.AccountStatusUpdateRequest;
import com.bff.services.server.models.AccountTypeCreateRequest;
import com.bff.services.server.models.AccountTypeResponse;
import com.bff.services.server.models.AccountTypeStatusUpdateRequest;
import com.bff.services.server.models.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountsController implements ChannelApi {

    private final AccountService accountService;
    private final AccountTypeService accountTypeService;

    @Override
    public ResponseEntity<AccountResponse> createAccount(String xDeviceIp, String xSession, AccountCreateRequest accountCreateRequest) {
        return accountService.createAccount(xDeviceIp, xSession, accountCreateRequest);
    }

    @Override
    public ResponseEntity<AccountTypeResponse> createAccountType(String xDeviceIp, String xSession, AccountTypeCreateRequest accountTypeCreateRequest) {
        return accountTypeService.createAccountType(xDeviceIp, xSession, accountTypeCreateRequest);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountById(String xDeviceIp, String xSession, Integer id) {
        return accountService.getAccountById(xDeviceIp, xSession, id);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountByNumber(String xDeviceIp, String xSession, String accountNumber) {
        return accountService.getAccountByNumber(xDeviceIp, xSession, accountNumber);
    }

    @Override
    public ResponseEntity<AccountTypeResponse> getAccountTypeById(String xDeviceIp, String xSession, Integer id) {
        return accountTypeService.getAccountTypeById(xDeviceIp, xSession, id);
    }

    @Override
    public ResponseEntity<List<AccountResponse>> listAccounts(String xDeviceIp, String xSession, Integer xPage, Integer xPageSize, String search, Integer idUser, Integer idAccountType, Status status) {
        return accountService.listAccounts(xDeviceIp, xSession, xPage, xPageSize, search, idUser, idAccountType, status);
    }

    @Override
    public ResponseEntity<List<AccountTypeResponse>> listAccountTypes(String xDeviceIp, String xSession, Integer xPage, Integer xPageSize, String search, Status status) {
        return accountTypeService.listAccountTypes(xDeviceIp, xSession, xPage, xPageSize, search, status);
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccountStatus(String xDeviceIp, String xSession, Integer id, AccountStatusUpdateRequest accountStatusUpdateRequest) {
        return accountService.updateAccountStatus(xDeviceIp, xSession, id, accountStatusUpdateRequest);
    }

    @Override
    public ResponseEntity<AccountTypeResponse> updateAccountTypeStatus(String xDeviceIp, String xSession, Integer id, AccountTypeStatusUpdateRequest accountTypeStatusUpdateRequest) {
        return accountTypeService.updateAccountTypeStatus(xDeviceIp, xSession, id, accountTypeStatusUpdateRequest);
    }
}

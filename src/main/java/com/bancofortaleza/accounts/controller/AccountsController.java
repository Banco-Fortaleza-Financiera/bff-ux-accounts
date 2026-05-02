package com.bancofortaleza.accounts.controller;

import com.bff.services.server.models.AccountCreateRequest;
import com.bff.services.server.models.AccountResponse;
import com.bff.services.server.models.AccountStatusUpdateRequest;
import com.bff.services.server.models.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.bff.services.server.ChannelApi;

import java.util.List;

@RestController
public class AccountsController implements ChannelApi {

    @Override
    public ResponseEntity<AccountResponse> createAccount(String xDeviceIp, String xSession, AccountCreateRequest accountCreateRequest) {
        return ChannelApi.super.createAccount(xDeviceIp, xSession, accountCreateRequest);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountById(String xDeviceIp, String xSession, Integer id) {
        return ChannelApi.super.getAccountById(xDeviceIp, xSession, id);
    }

    @Override
    public ResponseEntity<AccountResponse> getAccountByNumber(String xDeviceIp, String xSession, String accountNumber) {
        return ChannelApi.super.getAccountByNumber(xDeviceIp, xSession, accountNumber);
    }

    @Override
    public ResponseEntity<List<AccountResponse>> listAccounts(String xDeviceIp, String xSession, Integer idUser, Integer idAccountType, Status status) {
        return ChannelApi.super.listAccounts(xDeviceIp, xSession, idUser, idAccountType, status);
    }

    @Override
    public ResponseEntity<AccountResponse> updateAccountStatus(String xDeviceIp, String xSession, Integer id, AccountStatusUpdateRequest accountStatusUpdateRequest) {
        return ChannelApi.super.updateAccountStatus(xDeviceIp, xSession, id, accountStatusUpdateRequest);
    }
}

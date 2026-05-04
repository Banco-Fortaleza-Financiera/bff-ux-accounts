package com.bancofortaleza.accounts.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bancofortaleza.accounts.services.AccountService;
import com.bancofortaleza.accounts.services.AccountTypeService;
import com.bff.services.server.models.AccountCreateRequest;
import com.bff.services.server.models.AccountResponse;
import com.bff.services.server.models.AccountStatusUpdateRequest;
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
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AccountsControllerTest {

    private static final String DEVICE_IP = "192.168.1.10";
    private static final String SESSION = "7f2c1a54-4cf0-4d8c-9b18-2a6a57e7f7f3";

    @Mock
    private AccountService accountService;

    @Mock
    private AccountTypeService accountTypeService;

    @InjectMocks
    private AccountsController controller;

    @Test
    void createAccountShouldDelegateToAccountService() {
        AccountCreateRequest request = new AccountCreateRequest();
        ResponseEntity<AccountResponse> expected = ResponseEntity.ok(new AccountResponse().id(1));
        when(accountService.createAccount(DEVICE_IP, SESSION, request)).thenReturn(expected);

        ResponseEntity<AccountResponse> result = controller.createAccount(DEVICE_IP, SESSION, request);

        assertThat(result).isSameAs(expected);
        verify(accountService).createAccount(DEVICE_IP, SESSION, request);
    }

    @Test
    void createAccountTypeShouldDelegateToAccountTypeService() {
        AccountTypeCreateRequest request = new AccountTypeCreateRequest();
        ResponseEntity<AccountTypeResponse> expected = ResponseEntity.ok(new AccountTypeResponse().id(1));
        when(accountTypeService.createAccountType(DEVICE_IP, SESSION, request)).thenReturn(expected);

        ResponseEntity<AccountTypeResponse> result = controller.createAccountType(DEVICE_IP, SESSION, request);

        assertThat(result).isSameAs(expected);
        verify(accountTypeService).createAccountType(DEVICE_IP, SESSION, request);
    }

    @Test
    void getAccountByIdShouldDelegateToAccountService() {
        ResponseEntity<AccountResponse> expected = ResponseEntity.ok(new AccountResponse().id(3));
        when(accountService.getAccountById(DEVICE_IP, SESSION, 3)).thenReturn(expected);

        ResponseEntity<AccountResponse> result = controller.getAccountById(DEVICE_IP, SESSION, 3);

        assertThat(result).isSameAs(expected);
        verify(accountService).getAccountById(DEVICE_IP, SESSION, 3);
    }

    @Test
    void getAccountByNumberShouldDelegateToAccountService() {
        ResponseEntity<AccountResponse> expected = ResponseEntity.ok(new AccountResponse().accountNumber("123"));
        when(accountService.getAccountByNumber(DEVICE_IP, SESSION, "123")).thenReturn(expected);

        ResponseEntity<AccountResponse> result = controller.getAccountByNumber(DEVICE_IP, SESSION, "123");

        assertThat(result).isSameAs(expected);
        verify(accountService).getAccountByNumber(DEVICE_IP, SESSION, "123");
    }

    @Test
    void getAccountTypeByIdShouldDelegateToAccountTypeService() {
        ResponseEntity<AccountTypeResponse> expected = ResponseEntity.ok(new AccountTypeResponse().id(4));
        when(accountTypeService.getAccountTypeById(DEVICE_IP, SESSION, 4)).thenReturn(expected);

        ResponseEntity<AccountTypeResponse> result = controller.getAccountTypeById(DEVICE_IP, SESSION, 4);

        assertThat(result).isSameAs(expected);
        verify(accountTypeService).getAccountTypeById(DEVICE_IP, SESSION, 4);
    }

    @Test
    void listAccountsShouldDelegateToAccountService() {
        ResponseEntity<List<AccountResponse>> expected = ResponseEntity.ok(List.of(new AccountResponse().id(1)));
        when(accountService.listAccounts(DEVICE_IP, SESSION, 1, 20, "abc", 10, 2, Status.ACTIVE)).thenReturn(expected);

        ResponseEntity<List<AccountResponse>> result =
            controller.listAccounts(DEVICE_IP, SESSION, 1, 20, "abc", 10, 2, Status.ACTIVE);

        assertThat(result).isSameAs(expected);
        verify(accountService).listAccounts(DEVICE_IP, SESSION, 1, 20, "abc", 10, 2, Status.ACTIVE);
    }

    @Test
    void listAccountTypesShouldDelegateToAccountTypeService() {
        ResponseEntity<List<AccountTypeResponse>> expected = ResponseEntity.ok(List.of(new AccountTypeResponse().id(1)));
        when(accountTypeService.listAccountTypes(DEVICE_IP, SESSION, 1, 20, "abc", Status.ACTIVE)).thenReturn(expected);

        ResponseEntity<List<AccountTypeResponse>> result =
            controller.listAccountTypes(DEVICE_IP, SESSION, 1, 20, "abc", Status.ACTIVE);

        assertThat(result).isSameAs(expected);
        verify(accountTypeService).listAccountTypes(DEVICE_IP, SESSION, 1, 20, "abc", Status.ACTIVE);
    }

    @Test
    void updateAccountStatusShouldDelegateToAccountService() {
        AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
        ResponseEntity<AccountResponse> expected = ResponseEntity.ok(new AccountResponse().id(8));
        when(accountService.updateAccountStatus(DEVICE_IP, SESSION, 8, request)).thenReturn(expected);

        ResponseEntity<AccountResponse> result = controller.updateAccountStatus(DEVICE_IP, SESSION, 8, request);

        assertThat(result).isSameAs(expected);
        verify(accountService).updateAccountStatus(DEVICE_IP, SESSION, 8, request);
    }

    @Test
    void updateAccountTypeStatusShouldDelegateToAccountTypeService() {
        AccountTypeStatusUpdateRequest request = new AccountTypeStatusUpdateRequest();
        ResponseEntity<AccountTypeResponse> expected = ResponseEntity.ok(new AccountTypeResponse().id(9));
        when(accountTypeService.updateAccountTypeStatus(DEVICE_IP, SESSION, 9, request)).thenReturn(expected);

        ResponseEntity<AccountTypeResponse> result = controller.updateAccountTypeStatus(DEVICE_IP, SESSION, 9, request);

        assertThat(result).isSameAs(expected);
        verify(accountTypeService).updateAccountTypeStatus(DEVICE_IP, SESSION, 9, request);
    }
}

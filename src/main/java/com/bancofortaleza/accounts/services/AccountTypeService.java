package com.bancofortaleza.accounts.services;

import com.bff.services.server.models.AccountTypeCreateRequest;
import com.bff.services.server.models.AccountTypeResponse;
import com.bff.services.server.models.AccountTypeStatusUpdateRequest;
import com.bff.services.server.models.Status;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface AccountTypeService {

    ResponseEntity<List<AccountTypeResponse>> listAccountTypes(
        String xDeviceIp,
        String xSession,
        Integer xPage,
        Integer xPageSize,
        String search,
        Status status
    );

    ResponseEntity<AccountTypeResponse> createAccountType(
        String xDeviceIp,
        String xSession,
        AccountTypeCreateRequest accountTypeCreateRequest
    );

    ResponseEntity<AccountTypeResponse> getAccountTypeById(String xDeviceIp, String xSession, Integer id);

    ResponseEntity<AccountTypeResponse> updateAccountTypeStatus(
        String xDeviceIp,
        String xSession,
        Integer id,
        AccountTypeStatusUpdateRequest accountTypeStatusUpdateRequest
    );
}

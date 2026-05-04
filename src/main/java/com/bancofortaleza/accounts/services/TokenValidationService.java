package com.bancofortaleza.accounts.services;

public interface TokenValidationService {

    Integer validate(String authorizationHeader, String xDeviceIp, String xSession);
}

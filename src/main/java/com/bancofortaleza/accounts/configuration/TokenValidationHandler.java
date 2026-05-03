package com.bancofortaleza.accounts.configuration;

public interface TokenValidationHandler {

    Integer validate(String authorizationHeader, String xDeviceIp, String xSession);
}

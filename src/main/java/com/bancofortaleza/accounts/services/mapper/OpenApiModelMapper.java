package com.bancofortaleza.accounts.services.mapper;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Mapper(componentModel = "spring", nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface OpenApiModelMapper {

    Set<String> HOP_BY_HOP_HEADERS = Set.of(
        HttpHeaders.CONNECTION.toLowerCase(Locale.ROOT),
        HttpHeaders.CONTENT_LENGTH.toLowerCase(Locale.ROOT),
        HttpHeaders.TRANSFER_ENCODING.toLowerCase(Locale.ROOT),
        "keep-alive",
        "proxy-authenticate",
        "proxy-authorization",
        "te",
        "trailer",
        "upgrade"
    );

    com.bff.services.client.models.Status toClientStatus(com.bff.services.server.models.Status status);

    com.bff.services.client.models.AccountCreateRequest toClientAccountCreateRequest(
        com.bff.services.server.models.AccountCreateRequest request
    );

    com.bff.services.client.models.AccountStatusUpdateRequest toClientAccountStatusUpdateRequest(
        com.bff.services.server.models.AccountStatusUpdateRequest request
    );

    com.bff.services.client.models.AccountTypeCreateRequest toClientAccountTypeCreateRequest(
        com.bff.services.server.models.AccountTypeCreateRequest request
    );

    com.bff.services.client.models.AccountTypeStatusUpdateRequest toClientAccountTypeStatusUpdateRequest(
        com.bff.services.server.models.AccountTypeStatusUpdateRequest request
    );

    com.bff.services.server.models.AccountResponse toServerAccountResponse(
        com.bff.services.client.models.AccountResponse response
    );

    com.bff.services.server.models.AccountTypeResponse toServerAccountTypeResponse(
        com.bff.services.client.models.AccountTypeResponse response
    );

    List<com.bff.services.server.models.AccountResponse> toServerAccountResponses(
        List<com.bff.services.client.models.AccountResponse> responses
    );

    List<com.bff.services.server.models.AccountTypeResponse> toServerAccountTypeResponses(
        List<com.bff.services.client.models.AccountTypeResponse> responses
    );

    default ResponseEntity<com.bff.services.server.models.AccountResponse> mapAccountResponse(
        ResponseEntity<com.bff.services.client.models.AccountResponse> source
    ) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(sanitizeHeaders(source.getHeaders()))
            .body(toServerAccountResponse(source.getBody()));
    }

    default ResponseEntity<List<com.bff.services.server.models.AccountResponse>> mapAccountListResponse(
        ResponseEntity<List<com.bff.services.client.models.AccountResponse>> source
    ) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(sanitizeHeaders(source.getHeaders()))
            .body(toServerAccountResponses(source.getBody()));
    }

    default ResponseEntity<com.bff.services.server.models.AccountTypeResponse> mapAccountTypeResponse(
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> source
    ) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(sanitizeHeaders(source.getHeaders()))
            .body(toServerAccountTypeResponse(source.getBody()));
    }

    default ResponseEntity<List<com.bff.services.server.models.AccountTypeResponse>> mapAccountTypeListResponse(
        ResponseEntity<List<com.bff.services.client.models.AccountTypeResponse>> source
    ) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(sanitizeHeaders(source.getHeaders()))
            .body(toServerAccountTypeResponses(source.getBody()));
    }

    private HttpHeaders sanitizeHeaders(HttpHeaders source) {
        HttpHeaders headers = new HttpHeaders();
        source.forEach((name, values) -> {
            if (!isHopByHopHeader(name)) {
                values.forEach(value -> headers.add(name, value));
            }
        });
        return headers;
    }

    private boolean isHopByHopHeader(String name) {
        return HOP_BY_HOP_HEADERS.contains(name.toLowerCase(Locale.ROOT));
    }
}

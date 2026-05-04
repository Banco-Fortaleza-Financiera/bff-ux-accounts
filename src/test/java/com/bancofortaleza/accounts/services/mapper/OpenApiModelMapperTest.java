package com.bancofortaleza.accounts.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class OpenApiModelMapperTest {

    private final OpenApiModelMapper mapper = Mappers.getMapper(OpenApiModelMapper.class);

    @Test
    void toServerAccountResponseShouldReturnNullWhenSourceIsNull() {
        assertThat(mapper.toServerAccountResponse(null)).isNull();
    }

    @Test
    void toClientStatusShouldConvertServerStatusToClientStatus() {
        assertThat(mapper.toClientStatus(com.bff.services.server.models.Status.ACTIVE))
            .isEqualTo(com.bff.services.client.models.Status.ACTIVE);
        assertThat(mapper.toClientStatus(com.bff.services.server.models.Status.INACTIVE))
            .isEqualTo(com.bff.services.client.models.Status.INACTIVE);
        assertThat(mapper.toClientStatus(null)).isNull();
    }

    @Test
    void toClientAccountCreateRequestShouldConvertServerModelToClientModel() {
        com.bff.services.server.models.AccountCreateRequest source =
            new com.bff.services.server.models.AccountCreateRequest()
                .idAccountType(2)
                .idUser(10)
                .status(com.bff.services.server.models.Status.ACTIVE);

        com.bff.services.client.models.AccountCreateRequest result = mapper.toClientAccountCreateRequest(source);

        assertThat(result.getIdAccountType()).isEqualTo(2);
        assertThat(result.getIdUser()).isEqualTo(10);
        assertThat(result.getStatus()).isEqualTo(com.bff.services.client.models.Status.ACTIVE);
        assertThat(mapper.toClientAccountCreateRequest(null)).isNull();
    }

    @Test
    void toClientAccountStatusUpdateRequestShouldConvertServerModelToClientModel() {
        com.bff.services.server.models.AccountStatusUpdateRequest source =
            new com.bff.services.server.models.AccountStatusUpdateRequest()
                .status(com.bff.services.server.models.Status.INACTIVE);

        com.bff.services.client.models.AccountStatusUpdateRequest result =
            mapper.toClientAccountStatusUpdateRequest(source);

        assertThat(result.getStatus()).isEqualTo(com.bff.services.client.models.Status.INACTIVE);
        assertThat(mapper.toClientAccountStatusUpdateRequest(null)).isNull();
    }

    @Test
    void toClientAccountTypeCreateRequestShouldConvertServerModelToClientModel() {
        com.bff.services.server.models.AccountTypeCreateRequest source =
            new com.bff.services.server.models.AccountTypeCreateRequest()
                .accountType("SAVINGS")
                .accountCode("1001")
                .status(com.bff.services.server.models.Status.ACTIVE);

        com.bff.services.client.models.AccountTypeCreateRequest result =
            mapper.toClientAccountTypeCreateRequest(source);

        assertThat(result.getAccountType()).isEqualTo("SAVINGS");
        assertThat(result.getAccountCode()).isEqualTo("1001");
        assertThat(result.getStatus()).isEqualTo(com.bff.services.client.models.Status.ACTIVE);
        assertThat(mapper.toClientAccountTypeCreateRequest(null)).isNull();
    }

    @Test
    void toClientAccountTypeStatusUpdateRequestShouldConvertServerModelToClientModel() {
        com.bff.services.server.models.AccountTypeStatusUpdateRequest source =
            new com.bff.services.server.models.AccountTypeStatusUpdateRequest()
                .status(com.bff.services.server.models.Status.INACTIVE);

        com.bff.services.client.models.AccountTypeStatusUpdateRequest result =
            mapper.toClientAccountTypeStatusUpdateRequest(source);

        assertThat(result.getStatus()).isEqualTo(com.bff.services.client.models.Status.INACTIVE);
        assertThat(mapper.toClientAccountTypeStatusUpdateRequest(null)).isNull();
    }

    @Test
    void toServerAccountResponseShouldConvertClientModelToServerModel() {
        com.bff.services.client.models.AccountResponse source = new com.bff.services.client.models.AccountResponse()
            .id(1)
            .idUser(10)
            .idAccountType(2)
            .accountNumber("0010001234567890")
            .balance(BigDecimal.valueOf(1500.75))
            .status(com.bff.services.client.models.Status.ACTIVE);

        com.bff.services.server.models.AccountResponse result = mapper.toServerAccountResponse(source);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdUser()).isEqualTo(10);
        assertThat(result.getIdAccountType()).isEqualTo(2);
        assertThat(result.getAccountNumber()).isEqualTo("0010001234567890");
        assertThat(result.getBalance()).isEqualByComparingTo("1500.75");
        assertThat(result.getStatus()).isEqualTo(com.bff.services.server.models.Status.ACTIVE);
    }

    @Test
    void toServerAccountTypeResponseShouldConvertClientModelToServerModel() {
        com.bff.services.client.models.AccountTypeResponse source =
            new com.bff.services.client.models.AccountTypeResponse()
                .id(7)
                .accountType("CHECKING")
                .accountCode("2001")
                .status(com.bff.services.client.models.Status.INACTIVE);

        com.bff.services.server.models.AccountTypeResponse result = mapper.toServerAccountTypeResponse(source);

        assertThat(result.getId()).isEqualTo(7);
        assertThat(result.getAccountType()).isEqualTo("CHECKING");
        assertThat(result.getAccountCode()).isEqualTo("2001");
        assertThat(result.getStatus()).isEqualTo(com.bff.services.server.models.Status.INACTIVE);
        assertThat(mapper.toServerAccountTypeResponse(null)).isNull();
    }

    @Test
    void toServerAccountResponsesShouldReturnEmptyListWhenSourceIsNull() {
        assertThat(mapper.toServerAccountResponses(null)).isEmpty();
    }

    @Test
    void toServerAccountTypeResponsesShouldReturnEmptyListWhenSourceIsNull() {
        assertThat(mapper.toServerAccountTypeResponses(null)).isEmpty();
    }

    @Test
    void toServerAccountTypeResponsesShouldConvertEachElement() {
        List<com.bff.services.client.models.AccountTypeResponse> source = List.of(
            new com.bff.services.client.models.AccountTypeResponse()
                .id(1)
                .accountType("SAVINGS")
                .accountCode("1001")
                .status(com.bff.services.client.models.Status.ACTIVE)
        );

        List<com.bff.services.server.models.AccountTypeResponse> result = mapper.toServerAccountTypeResponses(source);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1);
        assertThat(result.getFirst().getAccountType()).isEqualTo("SAVINGS");
        assertThat(result.getFirst().getAccountCode()).isEqualTo("1001");
        assertThat(result.getFirst().getStatus()).isEqualTo(com.bff.services.server.models.Status.ACTIVE);
    }

    @Test
    void mapAccountResponseShouldPreserveStatusAndSanitizeHopByHopHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_LENGTH, "100");
        headers.add(HttpHeaders.CONNECTION, "keep-alive");
        headers.add("x-total-count", "3");
        com.bff.services.client.models.AccountResponse body = new com.bff.services.client.models.AccountResponse()
            .id(3)
            .accountNumber("777")
            .balance(BigDecimal.TEN)
            .status(com.bff.services.client.models.Status.INACTIVE);
        ResponseEntity<com.bff.services.client.models.AccountResponse> source =
            new ResponseEntity<>(body, headers, HttpStatus.CREATED);

        ResponseEntity<com.bff.services.server.models.AccountResponse> result = mapper.mapAccountResponse(source);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders()).containsEntry("x-total-count", List.of("3"));
        assertThat(result.getHeaders()).doesNotContainKey(HttpHeaders.CONTENT_LENGTH);
        assertThat(result.getHeaders()).doesNotContainKey(HttpHeaders.CONNECTION);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(3);
    }

    @Test
    void mapAccountListResponseShouldConvertBodyAndKeepAllowedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-page", "1");
        ResponseEntity<List<com.bff.services.client.models.AccountResponse>> source =
            new ResponseEntity<>(List.of(new com.bff.services.client.models.AccountResponse().id(1)), headers, HttpStatus.OK);

        ResponseEntity<List<com.bff.services.server.models.AccountResponse>> result =
            mapper.mapAccountListResponse(source);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).containsEntry("x-page", List.of("1"));
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().getFirst().getId()).isEqualTo(1);
    }

    @Test
    void mapAccountTypeResponseShouldConvertBodyAndSanitizeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.TRANSFER_ENCODING, "chunked");
        headers.add("x-request-id", "abc");
        com.bff.services.client.models.AccountTypeResponse body =
            new com.bff.services.client.models.AccountTypeResponse()
                .id(4)
                .accountType("SAVINGS")
                .accountCode("1001")
                .status(com.bff.services.client.models.Status.ACTIVE);
        ResponseEntity<com.bff.services.client.models.AccountTypeResponse> source =
            new ResponseEntity<>(body, headers, HttpStatus.ACCEPTED);

        ResponseEntity<com.bff.services.server.models.AccountTypeResponse> result =
            mapper.mapAccountTypeResponse(source);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(result.getHeaders()).containsEntry("x-request-id", List.of("abc"));
        assertThat(result.getHeaders()).doesNotContainKey(HttpHeaders.TRANSFER_ENCODING);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(4);
    }

    @Test
    void mapAccountTypeListResponseShouldConvertBodyAndKeepAllowedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-count", "1");
        ResponseEntity<List<com.bff.services.client.models.AccountTypeResponse>> source =
            new ResponseEntity<>(
                List.of(new com.bff.services.client.models.AccountTypeResponse().id(5)),
                headers,
                HttpStatus.OK
            );

        ResponseEntity<List<com.bff.services.server.models.AccountTypeResponse>> result =
            mapper.mapAccountTypeListResponse(source);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).containsEntry("x-total-count", List.of("1"));
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().getFirst().getId()).isEqualTo(5);
    }
}

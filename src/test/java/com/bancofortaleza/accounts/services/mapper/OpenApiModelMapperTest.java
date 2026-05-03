package com.bancofortaleza.accounts.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class OpenApiModelMapperTest {

    private final OpenApiModelMapper mapper = new OpenApiModelMapper(new ObjectMapper());

    @Test
    void mapShouldReturnNullWhenSourceIsNull() {
        assertThat(mapper.map(null, com.bff.services.server.models.AccountResponse.class)).isNull();
    }

    @Test
    void mapShouldConvertClientModelToServerModel() {
        com.bff.services.client.models.AccountResponse source = new com.bff.services.client.models.AccountResponse()
            .id(1)
            .idUser(10)
            .idAccountType(2)
            .accountNumber("0010001234567890")
            .balance(BigDecimal.valueOf(1500.75))
            .status(com.bff.services.client.models.Status.ACTIVE);

        com.bff.services.server.models.AccountResponse result =
            mapper.map(source, com.bff.services.server.models.AccountResponse.class);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getIdUser()).isEqualTo(10);
        assertThat(result.getIdAccountType()).isEqualTo(2);
        assertThat(result.getAccountNumber()).isEqualTo("0010001234567890");
        assertThat(result.getBalance()).isEqualByComparingTo("1500.75");
        assertThat(result.getStatus()).isEqualTo(com.bff.services.server.models.Status.ACTIVE);
    }

    @Test
    void mapListShouldReturnEmptyListWhenSourceIsNull() {
        assertThat(mapper.mapList(null, com.bff.services.server.models.AccountResponse.class)).isEmpty();
    }

    @Test
    void mapListShouldConvertEachElement() {
        List<com.bff.services.client.models.AccountTypeResponse> source = List.of(
            new com.bff.services.client.models.AccountTypeResponse()
                .id(1)
                .accountType("SAVINGS")
                .accountCode("1001")
                .status(com.bff.services.client.models.Status.ACTIVE)
        );

        List<com.bff.services.server.models.AccountTypeResponse> result =
            mapper.mapList(source, com.bff.services.server.models.AccountTypeResponse.class);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1);
        assertThat(result.getFirst().getAccountType()).isEqualTo("SAVINGS");
        assertThat(result.getFirst().getAccountCode()).isEqualTo("1001");
        assertThat(result.getFirst().getStatus()).isEqualTo(com.bff.services.server.models.Status.ACTIVE);
    }

    @Test
    void mapResponseShouldPreserveStatusAndSanitizeHopByHopHeaders() {
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

        ResponseEntity<com.bff.services.server.models.AccountResponse> result =
            mapper.mapResponse(source, com.bff.services.server.models.AccountResponse.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders()).containsEntry("x-total-count", List.of("3"));
        assertThat(result.getHeaders()).doesNotContainKey(HttpHeaders.CONTENT_LENGTH);
        assertThat(result.getHeaders()).doesNotContainKey(HttpHeaders.CONNECTION);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(3);
    }

    @Test
    void mapListResponseShouldConvertBodyAndKeepAllowedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-page", "1");
        ResponseEntity<List<com.bff.services.client.models.AccountResponse>> source =
            new ResponseEntity<>(List.of(new com.bff.services.client.models.AccountResponse().id(1)), headers, HttpStatus.OK);

        ResponseEntity<List<com.bff.services.server.models.AccountResponse>> result =
            mapper.mapListResponse(source, com.bff.services.server.models.AccountResponse.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders()).containsEntry("x-page", List.of("1"));
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().getFirst().getId()).isEqualTo(1);
    }
}

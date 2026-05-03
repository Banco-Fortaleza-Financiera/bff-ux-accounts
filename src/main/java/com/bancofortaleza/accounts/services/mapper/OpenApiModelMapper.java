package com.bancofortaleza.accounts.services.mapper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OpenApiModelMapper {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
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

    private final ObjectMapper objectMapper;

    public OpenApiModelMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T map(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        return objectMapper.convertValue(source, targetType);
    }

    public <T> List<T> mapList(Object source, Class<T> targetType) {
        if (source == null) {
            return List.of();
        }
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, targetType);
        return objectMapper.convertValue(source, listType);
    }

    public <T> ResponseEntity<T> mapResponse(ResponseEntity<?> source, Class<T> targetType) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(sanitizeHeaders(source.getHeaders()))
            .body(map(source.getBody(), targetType));
    }

    public <T> ResponseEntity<List<T>> mapListResponse(ResponseEntity<?> source, Class<T> targetType) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(sanitizeHeaders(source.getHeaders()))
            .body(mapList(source.getBody(), targetType));
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

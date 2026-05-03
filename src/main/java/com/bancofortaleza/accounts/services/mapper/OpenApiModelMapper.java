package com.bancofortaleza.accounts.services.mapper;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OpenApiModelMapper {

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
            .headers(source.getHeaders())
            .body(map(source.getBody(), targetType));
    }

    public <T> ResponseEntity<List<T>> mapListResponse(ResponseEntity<?> source, Class<T> targetType) {
        return ResponseEntity
            .status(source.getStatusCode())
            .headers(source.getHeaders())
            .body(mapList(source.getBody(), targetType));
    }
}

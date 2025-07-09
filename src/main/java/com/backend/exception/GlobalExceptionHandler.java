package com.backend.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DatasetNotFoundException.class)
    public ResponseEntity<?> handleDatasetNotFound(DatasetNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Dataset not found", ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Entity not found", ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", ex);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<?> handleJsonParse(JsonParseException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid JSON format", ex);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<?> handleJsonMapping(JsonMappingException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "JSON mapping error", ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArg(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid input", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad request", ex);
    }

//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(Map.of(
//                        "message", ex.getMessage(),
//                        "status", HttpStatus.BAD_REQUEST.value()
//                ));
//    }

    private ResponseEntity<?> buildResponse(HttpStatus status, String message, Exception ex) {
        log.error("Exception caught: ", ex);
        Map<String, Object> res = new HashMap<>();
        res.put("status", status.value());
        res.put("error", status.getReasonPhrase());
        res.put("message", message);
        res.put("details", ex.getMessage());
        return ResponseEntity.status(status).body(res);
    }
}
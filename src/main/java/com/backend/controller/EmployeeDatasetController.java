package com.backend.controller;

import com.backend.exception.BadRequestException;
import com.backend.model.EmployeeDatasetRecord;
import com.backend.service.impl.EmployeeDatasetServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/dataset")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Dataset controller", description = "APIs for managing and querying dynamic employee dataset records")
public class EmployeeDatasetController {

    private final EmployeeDatasetServiceImpl datasetService;

    @PostMapping("/{datasetName}/record")
    @Operation(summary = "Insert a new record into dataset",
            description = "Adds a new JSON-formatted record into the specified dataset. The record is dynamically structured and saved with a unique identifier.")
    public ResponseEntity<?> insertRecord(@PathVariable String datasetName,
                                          @RequestBody Map<String, Object> json) throws JsonProcessingException {
        log.info("Inserting record into dataset: {}", datasetName);
        log.debug("Record content: {}", json);

        EmployeeDatasetRecord saved = datasetService.insertRecord(datasetName, json);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Record added successfully");
        response.put("dataset", datasetName);
        response.put("recordId", saved.getId());

        log.info("Record inserted successfully with ID: {}", saved.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{datasetName}/query")
    @Operation(
            summary = "Query dataset records",
            description = "Performs dynamic querying on the specified dataset. Supports grouping by a field (`groupBy`) or sorting by a field (`sortBy`) with optional order (`asc` or `desc`)."
    )
    public ResponseEntity<?> query(@PathVariable String datasetName,
                                   @RequestParam(required = false) String groupBy,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false, defaultValue = "asc") String order) throws Exception {

        log.info("Querying dataset: {}", datasetName);

        if (groupBy != null) {
            log.info("Grouping records by: {}", groupBy);
            Map<String, List<Map<String, Object>>> result = datasetService.queryGroupBy(datasetName, groupBy);
            return ResponseEntity.ok(Map.of("groupedRecords", result));
        } else if (sortBy != null) {
            log.info("Sorting records by: {} in {} order", sortBy, order);
            List<Map<String, Object>> result = datasetService.querySortBy(datasetName, sortBy, order);
            return ResponseEntity.ok(Map.of("sortedRecords", result));
        } else {
            log.warn("Bad request: neither groupBy nor sortBy provided");
            throw new BadRequestException("Provide either groupBy or sortBy");
        }
    }
}

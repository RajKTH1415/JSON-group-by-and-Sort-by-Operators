package com.backend.service.impl;

import com.backend.model.EmployeeDatasetRecord;
import com.backend.repository.EmployeeDatasetRecordRepository;
import com.backend.service.DatasetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeDatasetServiceImpl implements DatasetService {

    private final EmployeeDatasetRecordRepository employeeDatasetRecordRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public EmployeeDatasetRecord insertRecord(String datasetName, Map<String, Object> json) throws JsonProcessingException {
        log.info("Inserting new record into dataset: {}", datasetName);
        log.debug("Record data: {}", json);

        String rowJson = mapper.writeValueAsString(json);

        EmployeeDatasetRecord record = EmployeeDatasetRecord.builder()
                .datasetName(datasetName)
                .jsonData(rowJson)
                .build();

        EmployeeDatasetRecord savedRecord = employeeDatasetRecordRepository.save(record);

        log.info("Record saved with ID: {}", savedRecord.getId());
        return savedRecord;
    }

    @Override
    public Map<String, List<Map<String, Object>>> queryGroupBy(String datasetName, String groupBy) throws JsonProcessingException {
        log.info("Querying dataset '{}' grouped by '{}'", datasetName, groupBy);

        List<EmployeeDatasetRecord> records = employeeDatasetRecordRepository.findByDatasetName(datasetName);
        log.debug("Found {} records for dataset '{}'", records.size(), datasetName);

        List<Map<String, Object>> recordList = new ArrayList<>();
        for (EmployeeDatasetRecord record : records) {
            Map<String, Object> map = mapper.readValue(record.getJsonData(), new TypeReference<>() {});
            recordList.add(map);
        }

        Map<String, List<Map<String, Object>>> grouped = recordList.stream()
                .filter(r -> r.containsKey(groupBy))
                .collect(Collectors.groupingBy(r -> String.valueOf(r.get(groupBy))));

        log.info("Grouping completed with {} groups", grouped.size());
        return grouped;
    }

    @Override
    public List<Map<String, Object>> querySortBy(String datasetName, String sortBy, String order) throws JsonProcessingException {
        log.info("Querying dataset '{}' sorted by '{}' in order '{}'", datasetName, sortBy, order);

        List<EmployeeDatasetRecord> records = employeeDatasetRecordRepository.findByDatasetName(datasetName);
        log.debug("Found {} records for dataset '{}'", records.size(), datasetName);

        List<Map<String, Object>> recordList = new ArrayList<>();
        for (EmployeeDatasetRecord record : records) {
            Map<String, Object> map = mapper.readValue(record.getJsonData(), new TypeReference<>() {});
            recordList.add(map);
        }

        Comparator<Map<String, Object>> comparator = Comparator.comparing(
                m -> ((Comparable) m.get(sortBy))
        );

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        List<Map<String, Object>> sortedList = recordList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        log.info("Sorting completed. Returning {} records", sortedList.size());
        return sortedList;
    }
}

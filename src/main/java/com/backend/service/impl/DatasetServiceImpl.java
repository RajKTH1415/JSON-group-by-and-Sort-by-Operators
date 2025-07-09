package com.backend.service.impl;

import com.backend.exception.DatasetNotFoundException;
import com.backend.model.DatasetRecord;
import com.backend.model.DatasetRecord;
import com.backend.repository.DatasetRecordRepository;
import com.backend.service.DatasetService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRecordRepository datasetRecordRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public DatasetRecord insertRecord(String datasetName, Map<String, Object> json) throws JsonProcessingException {
        String rowJson = mapper.writeValueAsString(json);
        DatasetRecord record = DatasetRecord.builder()
                .datasetName(datasetName)
                .jsonData(rowJson)
                .build();
        return datasetRecordRepository.save(record);
    }

    @Override
    public Map<String, List<Map<String, Object>>> queryGroupBy(String datasetName, String groupBy) throws JsonProcessingException {
        List<DatasetRecord> records = datasetRecordRepository.findByDatasetName(datasetName);
        if (records.isEmpty()) throw new DatasetNotFoundException("No records found for dataset: " + datasetName);

        List<Map<String, Object>> recordList = new ArrayList<>();

        for (DatasetRecord record : records) {
            Map<String, Object> map = mapper.readValue(record.getJsonData(), new TypeReference<>() {
            });
            recordList.add(map);
        }
        return recordList.stream()
                .filter(r -> r.containsKey(groupBy))
                .collect(Collectors.groupingBy(r -> String.valueOf(r.get(groupBy))));
    }

    @Override
    public List<Map<String, Object>> querySortBy(String datasetName,   String sortBy, String order) throws JsonProcessingException {
        List<DatasetRecord> records = datasetRecordRepository.findByDatasetName(datasetName);
        List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();

        for (DatasetRecord record : records) {
            Map<String, Object> map = mapper.readValue(record.getJsonData(), new TypeReference<Map<String, Object>>() {});
            recordList.add(map);
        }

        Collections.sort(recordList, new Comparator<Map<String, Object>>() {
            @SuppressWarnings("unchecked")
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Object val1 = o1.get(sortBy);
                Object val2 = o2.get(sortBy);

                if (val1 == null && val2 == null) return 0;
                if (val1 == null) return "desc".equalsIgnoreCase(order) ? 1 : -1;
                if (val2 == null) return "desc".equalsIgnoreCase(order) ? -1 : 1;

                if (val1 instanceof Comparable && val2 instanceof Comparable) {
                    return ((Comparable) val1).compareTo(val2);
                }
                return 0;
            }
        });

        if ("desc".equalsIgnoreCase(order)) {
            Collections.reverse(recordList);
        }

        return recordList;
    }
}



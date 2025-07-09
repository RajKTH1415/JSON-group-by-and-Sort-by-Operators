package com.backend.DatasetServiceTest;

import com.backend.exception.DatasetNotFoundException;
import com.backend.model.DatasetRecord;
import com.backend.repository.DatasetRecordRepository;
import com.backend.service.impl.DatasetServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatasetServiceImplTest {

    @Mock
    private DatasetRecordRepository datasetRecordRepository;

    @InjectMocks
    private DatasetServiceImpl datasetService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        datasetService = new DatasetServiceImpl(datasetRecordRepository);
    }

    @Test
    void testInsertRecord() throws JsonProcessingException {
        String datasetName = "Employees";
        Map<String, Object> json = Map.of("name", "Raj", "age", 30);

        String expectedJson = objectMapper.writeValueAsString(json);
        DatasetRecord savedRecord = DatasetRecord.builder()
                .datasetName(datasetName)
                .jsonData(expectedJson)
                .build();

        when(datasetRecordRepository.save(any(DatasetRecord.class)))
                .thenReturn(savedRecord);

        DatasetRecord result = datasetService.insertRecord(datasetName, json);

        assertEquals(datasetName, result.getDatasetName());
        assertEquals(expectedJson, result.getJsonData());
        verify(datasetRecordRepository, times(1)).save(any(DatasetRecord.class));
    }

    @Test
    void testQueryGroupBy() throws JsonProcessingException {
        String datasetName = "Users";
        String groupBy = "city";

        List<DatasetRecord> mockRecords = List.of(
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"name\": \"Alice\", \"city\": \"NY\"}").build(),
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"name\": \"Bob\", \"city\": \"LA\"}").build(),
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"name\": \"Charlie\", \"city\": \"NY\"}").build()
        );

        when(datasetRecordRepository.findByDatasetName(datasetName)).thenReturn(mockRecords);

        Map<String, List<Map<String, Object>>> result = datasetService.queryGroupBy(datasetName, groupBy);

        assertEquals(2, result.size());
        assertEquals(2, result.get("NY").size());
        assertEquals(1, result.get("LA").size());
        verify(datasetRecordRepository, times(1)).findByDatasetName(datasetName);
    }

    @Test
    void testQueryGroupByNoRecords() {
        String datasetName = "NonExisting";
        when(datasetRecordRepository.findByDatasetName(datasetName)).thenReturn(Collections.emptyList());

        assertThrows(DatasetNotFoundException.class, () -> {
            datasetService.queryGroupBy(datasetName, "anyField");
        });

        verify(datasetRecordRepository, times(1)).findByDatasetName(datasetName);
    }

    @Test
    void testQuerySortByAsc() throws JsonProcessingException {
        String datasetName = "Products";
        String sortBy = "price";
        String order = "asc";

        List<DatasetRecord> mockRecords = List.of(
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"item\": \"A\", \"price\": 20}").build(),
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"item\": \"B\", \"price\": 10}").build()
        );

        when(datasetRecordRepository.findByDatasetName(datasetName)).thenReturn(mockRecords);

        List<Map<String, Object>> sorted = datasetService.querySortBy(datasetName, sortBy, order);

        assertEquals(10, sorted.get(0).get("price"));
        assertEquals(20, sorted.get(1).get("price"));
        verify(datasetRecordRepository, times(1)).findByDatasetName(datasetName);
    }

    @Test
    void testQuerySortByDesc() throws JsonProcessingException {
        String datasetName = "Products";
        String sortBy = "price";
        String order = "desc";

        List<DatasetRecord> mockRecords = List.of(
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"item\": \"A\", \"price\": 15}").build(),
                DatasetRecord.builder().datasetName(datasetName)
                        .jsonData("{\"item\": \"B\", \"price\": 25}").build()
        );

        when(datasetRecordRepository.findByDatasetName(datasetName)).thenReturn(mockRecords);

        List<Map<String, Object>> sorted = datasetService.querySortBy(datasetName, sortBy, order);

        assertEquals(25, sorted.get(0).get("price"));
        assertEquals(15, sorted.get(1).get("price"));
    }

    @Test
    void testQuerySortByHandlesNullValues() throws JsonProcessingException {
        String datasetName = "Items";
        String sortBy = "size";
        String order = "asc";

        List<DatasetRecord> mockRecords = List.of(
                DatasetRecord.builder().datasetName(datasetName).jsonData("{\"item\": \"Pen\", \"size\": null}").build(),
                DatasetRecord.builder().datasetName(datasetName).jsonData("{\"item\": \"Notebook\", \"size\": 100}").build()
        );

        when(datasetRecordRepository.findByDatasetName(datasetName)).thenReturn(mockRecords);

        List<Map<String, Object>> result = datasetService.querySortBy(datasetName, sortBy, order);

        // null comes first in asc due to logic
        assertNull(result.get(0).get("size"));
        assertEquals(100, result.get(1).get("size"));
    }
}
package com.backend.DatasetControllerTest;

import com.backend.controller.DatasetController;
import com.backend.model.DatasetRecord;
import com.backend.service.impl.DatasetServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@WebMvcTest(DatasetController.class)
public class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasetServiceImpl datasetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testInsertRecord() throws Exception {
        String datasetName = "employees";
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("name", "Raj");
        jsonData.put("age", 30);

        DatasetRecord mockedRecord = DatasetRecord.builder()
                .id(1L)
                .datasetName(datasetName)
                .jsonData(objectMapper.writeValueAsString(jsonData))
                .build();

        when(datasetService.insertRecord(eq(datasetName), anyMap())).thenReturn(mockedRecord);

        mockMvc.perform(post("/api/dataset/{datasetName}/record", datasetName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jsonData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Record added successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataset").value(datasetName))
                .andExpect(MockMvcResultMatchers.jsonPath("$.recordId").value(1));
    }

    @Test
    void testQueryGroupBy() throws Exception {
        String datasetName = "employees";
        String groupBy = "department";

        Map<String, List<Map<String, Object>>> mockedGroupedData = Map.of(
                "HR", List.of(Map.of("name", "Alice", "department", "HR")),
                "IT", List.of(Map.of("name", "Bob", "department", "IT"))
        );

        when(datasetService.queryGroupBy(eq(datasetName), eq(groupBy))).thenReturn(mockedGroupedData);

        mockMvc.perform(get("/api/dataset/{datasetName}/query", datasetName)
                        .param("groupBy", groupBy))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.groupedRecords.HR[0].name").value("Alice"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.groupedRecords.IT[0].name").value("Bob"));
    }

    @Test
    void testQuerySortByAsc() throws Exception {
        String datasetName = "products";
        String sortBy = "price";
        String order = "asc";

        List<Map<String, Object>> mockedSortedData = List.of(
                Map.of("item", "Pen", "price", 5),
                Map.of("item", "Notebook", "price", 10)
        );

        when(datasetService.querySortBy(eq(datasetName), eq(sortBy), eq(order))).thenReturn(mockedSortedData);

        mockMvc.perform(get("/api/dataset/{datasetName}/query", datasetName)
                        .param("sortBy", sortBy)
                        .param("order", order))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortedRecords[0].item").value("Pen"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortedRecords[1].item").value("Notebook"));
    }

    @Test
    void testQueryBadRequest() throws Exception {
        String datasetName = "employees";

        mockMvc.perform(get("/api/dataset/{datasetName}/query", datasetName))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Provide either groupBy or sortBy"));
    }
}
package com.backend.service;

import com.backend.model.EmployeeDatasetRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface DatasetService {

     EmployeeDatasetRecord insertRecord(String datasetName, Map<String, Object> json) throws JsonProcessingException;

     Map<String, List<Map<String, Object>>> queryGroupBy(String datasetName, String groupBy) throws JsonProcessingException;

     List<Map<String, Object>> querySortBy(String datasetName, String sortBy, String order) throws JsonProcessingException;
}

package com.backend.repository;

import com.backend.model.EmployeeDatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeDatasetRecordRepository extends JpaRepository<EmployeeDatasetRecord,Long> {
    List<EmployeeDatasetRecord> findByDatasetName(String datasetName);
}

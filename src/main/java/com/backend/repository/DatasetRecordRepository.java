package com.backend.repository;

import com.backend.model.DatasetRecord;
import com.backend.model.DatasetRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetRecordRepository extends JpaRepository<DatasetRecord,Long> {
    List<DatasetRecord> findByDatasetName(String datasetName);
}

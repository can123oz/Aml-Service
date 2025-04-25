package com.aml_service.repository;

import com.aml_service.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    Optional<TransactionEntity> findByReference(String reference);
}

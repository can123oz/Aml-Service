package com.aml_service.repository;

import com.aml_service.model.TransactionOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<TransactionOutboxEntity, String> {
    @Query(value = """
                    SELECT * FROM transaction_outbox o
                    WHERE o.status = :status
                    ORDER BY o.created_at
                    LIMIT :limit
                    FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<TransactionOutboxEntity> findAndLockBatch(
            @Param("status") String status,
            @Param("limit") int limit
    );

    @Modifying
    @Query(value = """
                     DELETE FROM transaction_outbox o
                                    WHERE o.status = 'PROCESSED'
                                      AND o.created_at < :cutoff
            """, nativeQuery = true)
    int deleteOldProcessed(
            @Param("cutoff") Instant cutoff
    );

}
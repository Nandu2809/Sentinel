package com.sentinel.monitoring.repository;

import com.sentinel.monitoring.domain.model.ApiRequestEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiRequestRepository extends JpaRepository<ApiRequestEntity, UUID> {

    @Query("""
        SELECT r FROM ApiRequestEntity r
        WHERE (:apiId IS NULL OR r.api.id = :apiId)
          AND (:userId IS NULL OR r.userId = :userId)
          AND (:clientIp IS NULL OR r.clientIp = :clientIp)
          AND (:statusCode IS NULL OR r.statusCode = :statusCode)
          AND (:startTime IS NULL OR r.timestamp >= :startTime)
          AND (:endTime IS NULL OR r.timestamp <= :endTime)
        """)
    Page<ApiRequestEntity> searchRequests(
            @Param("apiId") UUID apiId,
            @Param("userId") String userId,
            @Param("clientIp") String clientIp,
            @Param("statusCode") Integer statusCode,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );

    @Query("SELECT COUNT(r) FROM ApiRequestEntity r WHERE r.timestamp >= :since")
    long countRequestsSince(@Param("since") Instant since);

    @Query("SELECT AVG(r.latencyMs) FROM ApiRequestEntity r WHERE r.timestamp >= :since")
    Double getAverageLatencySince(@Param("since") Instant since);

    @Query("SELECT r.api.name AS apiName, COUNT(r) AS requestCount FROM ApiRequestEntity r WHERE r.api IS NOT NULL AND r.timestamp >= :since GROUP BY r.api.name ORDER BY requestCount DESC")
    List<Object[]> findTopUsedApisSince(@Param("since") Instant since, Pageable pageable);

    @Query("SELECT r.api.name AS apiName, AVG(r.latencyMs) AS avgLatency FROM ApiRequestEntity r WHERE r.api IS NOT NULL AND r.timestamp >= :since GROUP BY r.api.name ORDER BY avgLatency DESC")
    List<Object[]> findSlowestApisSince(@Param("since") Instant since, Pageable pageable);

    @Query("SELECT r.api.name AS apiName, COUNT(r) AS errorCount FROM ApiRequestEntity r WHERE r.api IS NOT NULL AND r.statusCode >= 400 AND r.timestamp >= :since GROUP BY r.api.name ORDER BY errorCount DESC")
    List<Object[]> findHighestErrorRateApisSince(@Param("since") Instant since, Pageable pageable);
}

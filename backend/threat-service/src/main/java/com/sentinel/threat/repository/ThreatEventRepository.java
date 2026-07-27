package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.ThreatEventEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThreatEventRepository extends JpaRepository<ThreatEventEntity, UUID> {

    @Query("""
        SELECT e FROM ThreatEventEntity e
        WHERE (:threatType IS NULL OR e.threatType = :threatType)
          AND (:severity IS NULL OR e.severity = :severity)
          AND (:clientIp IS NULL OR e.clientIp = :clientIp)
          AND (:startTime IS NULL OR e.createdAt >= :startTime)
          AND (:endTime IS NULL OR e.createdAt <= :endTime)
        """)
    Page<ThreatEventEntity> searchThreats(
            @Param("threatType") ThreatType threatType,
            @Param("severity") ThreatSeverity severity,
            @Param("clientIp") String clientIp,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );

    long countByCreatedAtAfter(Instant since);
    long countByClientIpAndCreatedAtAfter(String clientIp, Instant since);
    long countByEndpointAndCreatedAtAfter(String endpoint, Instant since);

    @Query("SELECT e.threatType AS type, COUNT(e) AS cnt FROM ThreatEventEntity e WHERE e.createdAt >= :since GROUP BY e.threatType ORDER BY cnt DESC")
    List<Object[]> findTopThreatTypesSince(@Param("since") Instant since, Pageable pageable);

    @Query("SELECT e.clientIp AS ip, COUNT(e) AS cnt FROM ThreatEventEntity e WHERE e.createdAt >= :since GROUP BY e.clientIp ORDER BY cnt DESC")
    List<Object[]> findTopAttackingIpsSince(@Param("since") Instant since, Pageable pageable);

    @Query("SELECT e.endpoint AS endpoint, COUNT(e) AS cnt FROM ThreatEventEntity e WHERE e.createdAt >= :since GROUP BY e.endpoint ORDER BY cnt DESC")
    List<Object[]> findTopTargetedEndpointsSince(@Param("since") Instant since, Pageable pageable);
}

package com.company.snackledger.server.repo;

import com.company.snackledger.server.model.Purchase;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByKioskIdAndRequestId(String kioskId, String requestId);

    long countByKioskId(String kioskId);

    List<Purchase> findTop10ByOrderByCompletedAtDesc();

    Optional<Purchase> findTopByKioskIdOrderByCompletedAtDesc(String kioskId);

    long countByCompletedAtGreaterThanEqual(OffsetDateTime completedAt);

    long countByKioskIdAndCompletedAtGreaterThanEqual(String kioskId, OffsetDateTime completedAt);

    @Query("select coalesce(sum(p.purchaseTotal), 0) from Purchase p where p.completedAt >= :start")
    Optional<BigDecimal> sumPurchaseTotalSince(@Param("start") OffsetDateTime start);
}

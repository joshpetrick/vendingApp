package com.company.snackledger.server.repo;

import com.company.snackledger.server.model.Purchase;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Optional<Purchase> findByKioskIdAndRequestId(String kioskId, String requestId);

    long countByKioskId(String kioskId);
}

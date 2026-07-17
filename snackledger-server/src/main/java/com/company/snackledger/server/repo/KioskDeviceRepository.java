package com.company.snackledger.server.repo;

import com.company.snackledger.server.model.KioskDevice;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KioskDeviceRepository extends JpaRepository<KioskDevice, Long> {
    Optional<KioskDevice> findByKioskIdentifier(String kioskIdentifier);
}

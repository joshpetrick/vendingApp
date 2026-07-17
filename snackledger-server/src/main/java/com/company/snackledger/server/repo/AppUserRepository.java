package com.company.snackledger.server.repo;

import com.company.snackledger.server.model.AppUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByBadgeId(String badgeId);

    List<AppUser> findTop10ByDisplayNameContainingIgnoreCase(String query);
}

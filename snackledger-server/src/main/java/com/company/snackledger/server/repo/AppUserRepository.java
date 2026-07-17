package com.company.snackledger.server.repo;

import com.company.snackledger.server.model.AppUser;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByBadgeId(String badgeId);

    List<AppUser> findTop10ByDisplayNameContainingIgnoreCase(String query);

    List<AppUser> findTop50ByOrderByActiveDescDisplayNameAsc();

    long countByActiveTrue();

    long countByActiveFalse();

    long countByBalanceLessThan(BigDecimal balance);

    long countByBalanceGreaterThan(BigDecimal balance);

    @Query("select coalesce(sum(u.balance), 0) from AppUser u where u.balance < 0")
    Optional<BigDecimal> sumNegativeBalances();

    @Query("select coalesce(sum(u.balance), 0) from AppUser u where u.balance > 0")
    Optional<BigDecimal> sumPositiveBalances();
}

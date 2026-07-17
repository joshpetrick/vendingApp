package com.company.snackledger.server.repo;

import com.company.snackledger.server.model.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByBarcode(String barcode);

    List<Item> findTop10ByNameContainingIgnoreCase(String query);

    List<Item> findTop50ByOrderByActiveAscNameAsc();

    long countByActiveTrue();

    long countByActiveFalse();
}

package com.company.snackledger.server.web;

import com.company.snackledger.server.repo.AppUserRepository;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TvDashboardController {
    private final AppUserRepository users;

    TvDashboardController(AppUserRepository users) {
        this.users = users;
    }

    @GetMapping("/dashboard/tv")
    Object tv() {
        return Map.of("title", "SnackLedger TV Dashboard", "balances", users.findAll());
    }
}

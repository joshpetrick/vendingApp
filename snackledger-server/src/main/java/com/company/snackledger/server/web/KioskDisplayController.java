package com.company.snackledger.server.web;

import com.company.snackledger.server.config.KioskDisplayProperties;
import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KioskDisplayController {
    private final AppUserRepository users;
    private final ItemRepository items;
    private final KioskDisplayProperties displayProperties;

    KioskDisplayController(
            AppUserRepository users, ItemRepository items, KioskDisplayProperties displayProperties) {
        this.users = users;
        this.items = items;
        this.displayProperties = displayProperties;
    }

    @GetMapping("/kiosk")
    String kiosk(Model model) {
        model.addAttribute("users", users.findAll());
        model.addAttribute("items", items.findAll());
        model.addAttribute("cardsPerPage", displayProperties.getCardsPerPage());
        model.addAttribute("balancePageMillis", displayProperties.getBalancePageSeconds() * 1000);
        model.addAttribute("snackPageMillis", displayProperties.getSnackPageSeconds() * 1000);
        return "kiosk/display";
    }
}

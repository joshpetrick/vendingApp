package com.company.snackledger.server.web;

import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KioskDisplayController {
    private final AppUserRepository users;
    private final ItemRepository items;

    KioskDisplayController(AppUserRepository users, ItemRepository items) {
        this.users = users;
        this.items = items;
    }

    @GetMapping("/kiosk")
    String kiosk(Model model) {
        model.addAttribute("users", users.findAll());
        model.addAttribute("items", items.findAll());
        return "kiosk/display";
    }
}

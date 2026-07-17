package com.company.snackledger.server.web;

import com.company.snackledger.server.admin.AdminOverviewService;
import com.company.snackledger.server.config.KioskDisplayProperties;
import com.company.snackledger.server.model.AppUser;
import com.company.snackledger.server.model.Item;
import com.company.snackledger.server.repo.AppUserRepository;
import com.company.snackledger.server.repo.ItemRepository;
import com.company.snackledger.server.repo.KioskDeviceRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    private final AppUserRepository users;
    private final ItemRepository items;
    private final KioskDeviceRepository kiosks;
    private final KioskDisplayProperties kioskDisplayProperties;
    private final AdminOverviewService adminOverviewService;

    AdminController(
            AppUserRepository users,
            ItemRepository items,
            KioskDeviceRepository kiosks,
            KioskDisplayProperties kioskDisplayProperties,
            AdminOverviewService adminOverviewService) {
        this.users = users;
        this.items = items;
        this.kiosks = kiosks;
        this.kioskDisplayProperties = kioskDisplayProperties;
        this.adminOverviewService = adminOverviewService;
    }

    @GetMapping("/admin")
    String home(Model model) {
        model.addAttribute("overview", adminOverviewService.getOverview());
        return "admin/home";
    }

    @GetMapping("/admin/users")
    String users(Model model) {
        model.addAttribute("users", users.findAll());
        return "admin/users";
    }

    @PostMapping("/admin/users")
    String saveUser(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("username") String username,
            @RequestParam("displayName") String displayName,
            @RequestParam("badgeId") String badgeId,
            @RequestParam("balance") BigDecimal balance,
            @RequestParam(value = "active", defaultValue = "false") boolean active) {
        var user = id == null ? new AppUser() : users.findById(id).orElseGet(AppUser::new);
        user.username = username;
        user.displayName = displayName;
        user.badgeId = badgeId;
        user.balance = balance;
        user.active = active;
        users.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/delete")
    String deleteUser(@PathVariable("id") Long id) {
        users.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping({"/admin/items", "/admin/snacks"})
    String items(Model model) {
        model.addAttribute("items", items.findAll());
        return "admin/items";
    }

    @PostMapping({"/admin/items", "/admin/snacks"})
    String saveItem(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("barcode") String barcode,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "active", defaultValue = "false") boolean active) {
        var item = id == null ? new Item() : items.findById(id).orElseGet(Item::new);
        item.name = name;
        item.barcode = barcode;
        item.price = price;
        item.imageUrl = imageUrl == null || imageUrl.isBlank() ? null : imageUrl;
        item.active = active;
        items.save(item);
        return "redirect:/admin/items";
    }

    @PostMapping("/admin/items/{id}/delete")
    String deleteItem(@PathVariable("id") Long id) {
        items.deleteById(id);
        return "redirect:/admin/items";
    }

    @GetMapping({"/admin/kiosk-settings", "/admin/kiosks", "/admin/settings"})
    String kioskSettings(Model model) {
        model.addAttribute("settings", kioskDisplayProperties);
        model.addAttribute("kiosks", kiosks.findAll());
        return "admin/kiosk-settings";
    }

    @PostMapping({"/admin/kiosk-settings", "/admin/kiosks", "/admin/settings"})
    String saveKioskSettings(
            @RequestParam("cardsPerPage") int cardsPerPage,
            @RequestParam("balancePageSeconds") int balancePageSeconds,
            @RequestParam("snackPageSeconds") int snackPageSeconds) {
        kioskDisplayProperties.setCardsPerPage(cardsPerPage);
        kioskDisplayProperties.setBalancePageSeconds(balancePageSeconds);
        kioskDisplayProperties.setSnackPageSeconds(snackPageSeconds);
        return "redirect:/admin/kiosk-settings";
    }
    @GetMapping({"/admin/deposits", "/admin/ledger", "/admin/notifications", "/admin/activity"})
    String placeholder(Model model, jakarta.servlet.http.HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURI());
        return "admin/placeholder";
    }

}

package com.company.snackledger.server;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminWebTests {
    @Autowired private MockMvc mvc;

    @Test
    void adminUserIsSentToAdminHomepageAfterLogin() throws Exception {
        mvc.perform(post("/login").param("username", "admin").param("password", "admin-change-me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void kioskUserStillGoesToKioskAfterLogin() throws Exception {
        mvc.perform(post("/login").param("username", "kiosk").param("password", "kiosk-change-me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/kiosk"));
    }

    @Test
    void adminHomepageLinksToCoreAdminWorkflows() throws Exception {
        mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/home"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Manage user accounts")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Manage snacks")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Kiosk settings")));
    }

    @Test
    void kioskUserCannotOpenAdminHomepage() throws Exception {
        mvc.perform(get("/admin").with(user("kiosk").roles("KIOSK"))).andExpect(status().isForbidden());
    }
}

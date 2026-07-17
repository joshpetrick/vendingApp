package com.company.snackledger.server;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class KioskDisplayTests {
    @Autowired private MockMvc mvc;

    @Test
    void kioskUserCanOpenReadOnlyDisplay() throws Exception {
        mvc.perform(get("/kiosk").with(user("kiosk").roles("KIOSK")))
                .andExpect(status().isOk())
                .andExpect(view().name("kiosk/display"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("snack-image")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("negative")));
    }

    @Test
    void anonymousUserMustLogInBeforeViewingKioskDisplay() throws Exception {
        mvc.perform(get("/kiosk")).andExpect(status().is3xxRedirection());
    }
}

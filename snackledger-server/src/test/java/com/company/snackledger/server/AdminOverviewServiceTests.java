package com.company.snackledger.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.snackledger.server.admin.AdminOverviewService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AdminOverviewServiceTests {
    @Autowired private AdminOverviewService overviewService;

    @Test
    void calculatesDashboardSummaryWithoutExposingNegativeDebtAsNegativeAmount() {
        var overview = overviewService.getOverview();

        assertThat(overview.summary().activeUsers()).isGreaterThan(0);
        assertThat(overview.summary().totalAmountOwed()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(overview.summary().totalUserCredit()).isGreaterThan(BigDecimal.ZERO);
        assertThat(overview.userPreview()).isNotEmpty();
        assertThat(overview.snackPreview()).isNotEmpty();
    }
}

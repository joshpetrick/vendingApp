package com.company.snackledger.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snackledger.kiosk-display")
public class KioskDisplayProperties {
    private int balancePageSeconds = 5;
    private int snackPageSeconds = 5;
    private int cardsPerPage = 6;

    public int getBalancePageSeconds() {
        return balancePageSeconds;
    }

    public void setBalancePageSeconds(int balancePageSeconds) {
        this.balancePageSeconds = balancePageSeconds;
    }

    public int getSnackPageSeconds() {
        return snackPageSeconds;
    }

    public void setSnackPageSeconds(int snackPageSeconds) {
        this.snackPageSeconds = snackPageSeconds;
    }

    public int getCardsPerPage() {
        return cardsPerPage;
    }

    public void setCardsPerPage(int cardsPerPage) {
        this.cardsPerPage = cardsPerPage;
    }
}

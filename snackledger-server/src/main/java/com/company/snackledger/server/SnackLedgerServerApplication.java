package com.company.snackledger.server;

import org.springframework.boot.SpringApplication;
import com.company.snackledger.server.config.KioskDisplayProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(KioskDisplayProperties.class)
public class SnackLedgerServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SnackLedgerServerApplication.class, args);
    }
}

package com.crypto.crypto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController; // BU ÖNEMLİ!

import java.util.Collection;

@RestController // Sınıfın bir REST controller olduğunu belirtir.
@RequestMapping("/api/financials") // Sınıf seviyesindeki ana URL.
public class FinancialController {

    private final FinancialDataService financialDataService;

    public FinancialController(FinancialDataService financialDataService) {
        this.financialDataService = financialDataService;
    }

    @GetMapping("/crypto") // /api/financials/crypto URL'ini bu metoda bağlar.
    public Collection<LatestCryptoPrice> getCryptoPrices() {
        return financialDataService.getAllCryptoPrices();
    }

    @GetMapping("/forex") // /api/financials/forex URL'ini bu metoda bağlar.
    public Collection<LatestForexRate> getForexRates() {
        return financialDataService.getAllForexRates();
    }
}
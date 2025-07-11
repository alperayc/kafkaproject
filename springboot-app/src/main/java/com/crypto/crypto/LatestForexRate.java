package com.crypto.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LatestForexRate {

    @JsonProperty("FOREX_SYMBOL")
    private String forexSymbol;

    @JsonProperty("LATEST_RATE_USD")
    private double latestRateUsd;

    @JsonProperty("LAST_UPDATE")
    private String lastUpdate;
}
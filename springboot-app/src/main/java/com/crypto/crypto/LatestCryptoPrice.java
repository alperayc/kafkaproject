package com.crypto.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LatestCryptoPrice {

    @JsonProperty("CRYPTO_SYMBOL")
    private String cryptoSymbol;

    @JsonProperty("LATEST_PRICE_USD")
    private double latestPriceUsd;

    @JsonProperty("LAST_UPDATE")
    private String lastUpdate;
}
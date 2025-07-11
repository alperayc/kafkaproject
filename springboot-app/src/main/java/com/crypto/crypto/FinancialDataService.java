package com.crypto.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class FinancialDataService {

    // Gelen JSON'ı Java nesnesine çevirmek için standart bir Spring aracı
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Verileri saklamak için thread-safe (eş zamanlı erişime uygun) Map'ler.
    // Key: Sembol (örn: "BTC"), Value: Fiyat nesnesi
    private final Map<String, LatestCryptoPrice> cryptoPriceMap = new ConcurrentHashMap<>();
    private final Map<String, LatestForexRate> forexRateMap = new ConcurrentHashMap<>();

    /**
     * ksqlDB'nin LATEST_CRYPTO_PRICES topic'ini dinler.
     * Bu topic'e gelen her mesajı (JSON string'i) alır.
     *
     * @param message Kafka'dan gelen ham JSON mesajı.
     */
    @KafkaListener(topics = "LATEST_CRYPTO_PRICES", groupId = "final-crypto-project-group")
    public void consumeCryptoPrices(String message) {
        try {
            // Gelen JSON string'ini LatestCryptoPrice nesnesine dönüştürür.
            // Bu adımın başarılı olması için DTO'daki @JsonProperty anotasyonları kritik öneme sahiptir.
            LatestCryptoPrice cryptoPrice = objectMapper.readValue(message, LatestCryptoPrice.class);

            // Nesneye başarıyla dönüştüyse ve sembolü varsa, Map'e ekle/güncelle.
            if (cryptoPrice != null && cryptoPrice.getCryptoSymbol() != null) {
                cryptoPriceMap.put(cryptoPrice.getCryptoSymbol(), cryptoPrice);
                // HATA AYIKLAMA İÇİN: Başarılı işlemi konsola yazdır.
                System.out.println("Received and Processed Crypto: " + cryptoPrice);
            }
        } catch (JsonProcessingException e) {
            // JSON ayrıştırma sırasında bir hata olursa, bunu loglara yazdırır.
            System.err.println("Failed to parse crypto message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * ksqlDB'nin LATEST_FOREX_RATES topic'ini dinler.
     *
     * @param message Kafka'dan gelen ham JSON mesajı.
     */
    @KafkaListener(topics = "LATEST_FOREX_RATES", groupId = "final-crypto-project-group")
    public void consumeForexRates(String message) {
        try {
            // Gelen JSON string'ini LatestForexRate nesnesine dönüştürür.
            LatestForexRate forexRate = objectMapper.readValue(message, LatestForexRate.class);

            if (forexRate != null && forexRate.getForexSymbol() != null) {
                forexRateMap.put(forexRate.getForexSymbol(), forexRate);
                // HATA AYIKLAMA İÇİN: Başarılı işlemi konsola yazdır.
                System.out.println("Received and Processed Forex: " + forexRate);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse forex message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * Controller'ın, saklanan tüm kripto verilerini almasını sağlayan metot.
     *
     * @return Kripto fiyatları koleksiyonu.
     */
    public Collection<LatestCryptoPrice> getAllCryptoPrices() {
        return cryptoPriceMap.values();
    }

    /**
     * Controller'ın, saklanan tüm döviz verilerini almasını sağlayan metot.
     *
     * @return Döviz kurları koleksiyonu.
     */
    public Collection<LatestForexRate> getAllForexRates() {
        return forexRateMap.values();
    }
}
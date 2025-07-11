import time
import json
import requests
import os
from kafka import KafkaProducer
from dotenv import load_dotenv
from datetime import datetime

load_dotenv()

# --- Kafka Ayarları ---
KAFKA_SERVER = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
KAFKA_TOPIC = "financial_data_topic"

# --- API Ayarları ---
CRYPTOCOMPARE_API_KEY = os.getenv("CRYPTOCOMPARE_API_KEY")
if not CRYPTOCOMPARE_API_KEY:
    raise ValueError("CRYPTOCOMPARE_API_KEY bulunamadı! Lütfen .env dosyasını kontrol edin.")

CRYPTO_SYMBOLS = "BTC,ETH,SOL,XRP,ADA"
FIAT_SYMBOLS = "EUR,GBP,JPY"

# --- Kafka Producer'ı Oluşturma ---
producer = KafkaProducer(
    bootstrap_servers=KAFKA_SERVER,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)
print("Kafka Producer for CryptoCompare started.")

# --- Ana Veri Çekme Döngüsü ---
try:
    while True:
        print("\nFetching latest financial data...")
        try:
            url = f"https://min-api.cryptocompare.com/data/pricemulti?fsyms={CRYPTO_SYMBOLS},{FIAT_SYMBOLS}&tsyms=USD"
            headers = {"authorization": f"Apikey {CRYPTOCOMPARE_API_KEY}"}
            response = requests.get(url, headers=headers)
            response.raise_for_status()
            price_data = response.json()
            
            for symbol, data in price_data.items():
                price_usd = data.get('USD')
                if price_usd is not None:
                    data_type = "crypto" if symbol in CRYPTO_SYMBOLS else "forex"
                    message = {
                        'symbol': symbol,
                        'price_usd': price_usd,
                        'type': data_type,
                        'timestamp': datetime.now().isoformat()
                    }
                    producer.send(KAFKA_TOPIC, value=message)
                    print(f"Sent to Kafka: {message}")
            producer.flush()
        except Exception as e:
            print(f"An error occurred: {e}")
        
        # --- DEĞİŞİKLİK: Bekleme süresi 60 saniyeden 5 saniyeye düşürüldü ---
        print("\nWaiting for 5 seconds...")
        time.sleep(5) 

except KeyboardInterrupt:
    print("\nProducer stopped.")
finally:
    producer.close()
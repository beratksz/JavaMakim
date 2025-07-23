# 🚀 JavaMakim Production Deployment Rehberi

## 📋 Ön Gereksinimler

### Java Runtime Environment
- **Java 11 veya üstü** gereklidir
- OpenJDK veya Oracle JDK kullanabilirsiniz

### PostgreSQL Veritabanı
- **PostgreSQL 12+** önerilir
- Veritabanı ve kullanıcı oluşturulmalı
- Network erişimi açık olmalı

### Access Veritabanı Dosyaları
- MW301_DB25.mdb (Ana şube)
- MW301_DB25_SUBE2.mdb (2. şube)
- Dosyalar uygulama tarafından okunabilir olmalı

---

## 🔧 Windows Deployment

### Adım 1: Java Kurulumu
```cmd
# Oracle JDK veya OpenJDK 11+ indir ve kur
# JAVA_HOME environment variable'ını ayarla
java -version
```

### Adım 2: Projeyi Build Et
```cmd
# Proje klasörüne git
cd C:\path\to\JavaMakim

# Build script'i çalıştır
build.bat
```

### Adım 3: Production Konfigürasyonu
```cmd
# application-prod.properties dosyasını düzenle:
# - PostgreSQL bağlantı bilgileri
# - Access veritabanı dosya yolları
# - Log dosya yolları
```

### Adım 4: Servisi Başlat
```cmd
# JAR dosyasını target klasöründen kopyala
copy target\JavaMakim-0.0.1-SNAPSHOT.jar .

# Servisi başlat
start.bat
```

### Adım 5: Test Et
```cmd
# Tarayıcıda aç
http://localhost:9090

# API test et
curl http://localhost:9090/api/simple/status
```

---

## 🐧 Linux Deployment

### Adım 1: Sistem Hazırlığı
```bash
# Java kurulumu (Ubuntu/Debian)
sudo apt update
sudo apt install openjdk-11-jdk

# Kullanıcı oluştur
sudo useradd -r -m -d /opt/javamakım javamakım

# Dizin yapısını oluştur
sudo mkdir -p /opt/javamakım /var/log/javamakım
sudo chown javamakım:javamakım /opt/javamakım /var/log/javamakım
```

### Adım 2: Projeyi Deploy Et
```bash
# Build et
./build.sh

# JAR dosyasını kopyala
sudo cp target/JavaMakim-0.0.1-SNAPSHOT.jar /opt/javamakım/
sudo cp application-prod.properties /opt/javamakım/
sudo cp start.sh stop.sh /opt/javamakım/
sudo chmod +x /opt/javamakım/*.sh

# Access veritabanı dosyalarını kopyala
sudo cp MW301_DB25*.mdb /opt/javamakım/data/
sudo chown -R javamakım:javamakım /opt/javamakım/
```

### Adım 3: Systemd Service Kurulumu
```bash
# Service dosyasını kopyala
sudo cp javamakım.service /etc/systemd/system/

# Service'i etkinleştir
sudo systemctl daemon-reload
sudo systemctl enable javamakım
sudo systemctl start javamakım

# Durumu kontrol et
sudo systemctl status javamakım
```

### Adım 4: Test Et
```bash
# Service log'larını kontrol et
sudo journalctl -u javamakım -f

# API test et
curl http://localhost:9090/api/simple/status
```

---

## 🔧 Konfigürasyon Detayları

### PostgreSQL Ayarları
```properties
# application-prod.properties içinde:
spring.datasource.url=jdbc:postgresql://localhost:5432/javamakım
spring.datasource.username=javamakım_user
spring.datasource.password=secure_password
```

### Access Veritabanı Yolları
```properties
# Windows:
access.database.path1=C:\\data\\MW301_DB25.mdb
access.database.path2=C:\\data\\MW301_DB25_SUBE2.mdb

# Linux:
access.database.path1=/opt/javamakım/data/MW301_DB25.mdb
access.database.path2=/opt/javamakım/data/MW301_DB25_SUBE2.mdb
```

---

## 📊 Monitoring ve Bakım

### Sistem Durumu Kontrol
```bash
# Service durumu (Linux)
sudo systemctl status javamakım

# Process kontrol
ps aux | grep java

# Log takibi
tail -f /var/log/javamakım/application.log
```

### Web Interface
- Ana sayfa: `http://localhost:9090`
- Sistem durumu: `http://localhost:9090/api/simple/status`
- Performans: `http://localhost:9090/api/sync/status`

### Otomatik Senkronizasyon
- Her 5 dakikada bir otomatik çalışır
- Manuel senkronizasyon: `http://localhost:9090/api/sync/all`
- Tam sıfırlama: `http://localhost:9090/api/sync/full`

---

## 🚨 Troubleshooting

### Sık Karşılaşılan Sorunlar

1. **Java bulunamadı**
   ```bash
   # JAVA_HOME'u kontrol et
   echo $JAVA_HOME
   which java
   ```

2. **PostgreSQL bağlantı sorunu**
   ```bash
   # Bağlantıyı test et
   psql -h localhost -U javamakım_user -d javamakım
   ```

3. **Access veritabanı okunmuyor**
   ```bash
   # Dosya izinlerini kontrol et
   ls -la /opt/javamakım/data/
   sudo chown javamakım:javamakım *.mdb
   ```

4. **Port zaten kullanımda**
   ```bash
   # Portu değiştir (application-prod.properties)
   server.port=8080
   ```

### Log Analizi
```bash
# Hata logları
grep ERROR /var/log/javamakım/application.log

# Son 100 satır
tail -100 /var/log/javamakım/application.log

# Canlı takip
tail -f /var/log/javamakım/application.log
```

---

## 🔄 Güncelleme Prosedürü

1. **Servisi Durdur**
   ```bash
   sudo systemctl stop javamakım  # Linux
   # veya
   # Windows'da Task Manager'dan java process'ini kapat
   ```

2. **Yeni JAR'ı Deploy Et**
   ```bash
   ./build.sh
   sudo cp target/JavaMakim-0.0.1-SNAPSHOT.jar /opt/javamakım/
   ```

3. **Servisi Başlat**
   ```bash
   sudo systemctl start javamakım  # Linux
   # veya
   start.bat  # Windows
   ```

---

## 📞 Destek

Herhangi bir sorun yaşarsanız:
1. Log dosyalarını kontrol edin
2. Sistem gereksinimlerini doğrulayın
3. Konfigürasyon dosyalarını gözden geçirin
4. Test API endpoint'lerini kullanın

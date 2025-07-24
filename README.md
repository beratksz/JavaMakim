# Merkezi Geçiş Sistemi

**Modern Spring Boot uygulaması ile Access Database ve PostgreSQL entegrasyonu**

## 🚀 Özellikler

- **Dual Database Architecture**: PostgreSQL (personel) + MS Access (geçiş kayıtları, birden fazla şube)
- **Real-time Data Access**: Access DB'den canlı veri okuma
- **Smart Data Joining**: Geçiş kayıtları ile kullanıcı bilgilerinin otomatik birleştirilmesi
- **Advanced Filtering**: Kart ID, tarih aralığı ile filtreleme
- **Clean Data**: ID=0 olan kayıtların otomatik filtrelenmesi
- **Production Ready**: Hata yönetimi ve performans optimizasyonu
- **Branch-based Logging**: Geçiş kayıtları branchId ile ayrıştırılır, personel kaydı ise tekil tutulur. Bir personel farklı şubelerde geçiş yaparsa, PostgreSQL'de branchId ile ayrışan birden fazla geçiş kaydı oluşur.

## 🛠️ Teknoloji Stack

- **Backend**: Spring Boot 3.2.1
- **Database**: PostgreSQL + MS Access
- **ORM**: Hibernate 6.4.1
- **Access DB**: Jackcess 4.0.5
- **Frontend**: HTML5, CSS3, JavaScript
- **Java Version**: 17+

## 📊 API Endpoints

### Personel İşlemleri
```http
GET /api/simple/personel
```
PostgreSQL'deki tüm personel kayıtlarını getirir.

### Geçiş Kayıtları
```http
GET /api/simple/gecis?limit=100&kartId=665336&startDate=2021-12-01&endDate=2021-12-31
```

**Parametreler:**
- `limit`: Kaç kayıt getirileceği (varsayılan: 100)
- `kartId`: Belirli bir kart ID'si ile filtreleme
- `startDate`: Başlangıç tarihi (YYYY-MM-DD)
- `endDate`: Bitiş tarihi (YYYY-MM-DD)

### Sistem Durumu
```http
GET /api/simple/status
```
Sistem durumu, veritabanı bağlantıları ve kayıt sayıları.

## 🔧 Kurulum

1. **Gereksinimler**
   - Java 17+
   - Maven 3.6+
   - PostgreSQL
   - MS Access Database (.mdb dosyası)

2. **Yapılandırma**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:postgresql://host:port/database
   spring.datasource.username=username
   spring.datasource.password=password
   access.database.path1=C:\\path\\to\\database.mdb
   ```

3. **Çalıştırma**
   ```bash
   mvn spring-boot:run
   ```

4. **Test**
   - Uygulama: http://localhost:9090
   - API Test: http://localhost:9090/api/simple/status

## 📁 Proje Yapısı

```
src/
├── main/
│   ├── java/com/gecisystems/merkezi/
│   │   ├── controller/
│   │   │   └── SimpleAccessController.java    # Ana controller
│   │   ├── service/
│   │   │   └── SimpleAccessService.java       # İş mantığı
│   │   ├── entity/
│   │   │   ├── Personel.java                  # JPA entity
│   │   │   └── GecisKaydi.java               # JPA entity
│   │   ├── repository/
│   │   │   ├── PersonelRepository.java        # Data access
│   │   │   └── GecisKaydiRepository.java     # Data access
│   │   └── MerkeziApplication.java            # Ana uygulama
│   └── resources/
│       ├── static/
│       │   └── index.html                     # Web UI
│       └── application.properties             # Yapılandırma
```

## 🔄 Veri Akışı

1. **PostgreSQL** → Personel bilgileri (tekil, AccessId veya KartId ile eşleşir)
2. **Access DB (Şube 1 & Şube 2)** → Geçiş kayıtları (her şube ayrı Access DB'den okunur)
3. **BranchId ile Ayrışma** → Geçiş kayıtları branchId ile ayrıştırılır, bir personel farklı şubelerde geçiş yaparsa iki ayrı kayıt oluşur
4. **Join Operation** → Geçiş + Kullanıcı bilgileri
5. **Filtering** → Tarih, kart ID, temiz data
6. **REST API** → JSON response

## 📈 Performans

- **Memory Efficient**: Büyük Access DB'ler için optimize edilmiş
- **Fast Queries**: Index'li PostgreSQL sorguları
- **Smart Caching**: User bilgileri bellekte cache
- **Clean Data**: Gereksiz kayıtların filtrelenmesi

## 🔐 Güvenlik

- **CORS Enabled**: Cross-origin request desteği
- **Error Handling**: Global exception handling
- **Input Validation**: Parametre doğrulama
- **Connection Pooling**: Güvenli DB bağlantıları

## 🏁 Production


### Şube Mantığı ve Prod Senaryosu

Sistem, production ortamında iki farklı Access DB ile çalışacak şekilde tasarlanmıştır. Her şube için ayrı Access DB tanımlanır (`access.database.path1`, `access.database.path2`).

- Bir personel sabah şube 1'de, öğleden sonra şube 2'de geçiş yaparsa, PostgreSQL'de branchId ile ayrışan iki geçiş kaydı oluşur.
- Personel kaydı ise AccessId veya KartId ile tekil tutulur, tekrar eklenmez.
- Böylece, gerçek hayattaki hareketler ve şube bazlı geçişler doğru şekilde kaydedilir.

Sistem özellikleri:
- ✅ Clean code architecture
- ✅ Error handling
- ✅ Performance optimization
- ✅ Logging configuration
- ✅ Resource management
- ✅ Branch-based geçiş kaydı yönetimi

## 📞 İletişim

Bu sistem, Access Database ve PostgreSQL arasında köprü görevi gören modern bir Spring Boot uygulamasıdır.

---
**© 2025 Merkezi Geçiş Sistemi - Production Ready**

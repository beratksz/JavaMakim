# JavaMakim API Dokümantasyonu

Bu belge JavaMakim uygulamasının REST API endpoint'lerini açıklar.

## Base URL
```
http://localhost:8080/api/v1
```

## Users API Endpoints

### 1. Tüm Kullanıcıları Getir
```
GET /api/v1/users
```

**Query Parameters:**
- `page` (optional): Sayfa numarası (default: 0)
- `size` (optional): Sayfa boyutu (default: 50)
- `sortBy` (optional): Sıralama alanı (default: "id")
- `sortDir` (optional): Sıralama yönü "asc" veya "desc" (default: "asc")

**Örnek:**
```
GET /api/v1/users?page=0&size=10&sortBy=adi&sortDir=asc
```

### 2. ID'ye Göre Kullanıcı Getir
```
GET /api/v1/users/{id}
```

**Örnek:**
```
GET /api/v1/users/123
```

### 3. Access ID'ye Göre Kullanıcı Getir
```
GET /api/v1/users/by-access-id/{accessId}
```

**Örnek:**
```
GET /api/v1/users/by-access-id/456
```

### 4. Kart ID'ye Göre Kullanıcı Getir
```
GET /api/v1/users/by-card/{kartId}
```

**Örnek:**
```
GET /api/v1/users/by-card/CARD123456
```

### 5. TC Kimlik'e Göre Kullanıcı Getir
```
GET /api/v1/users/by-tc/{tcKimlik}
```

**Örnek:**
```
GET /api/v1/users/by-tc/12345678901
```

### 6. Şubeye Göre Kullanıcıları Getir
```
GET /api/v1/users/by-branch/{branchId}
```

**Örnek:**
```
GET /api/v1/users/by-branch/1
```

### 7. Aktif Kullanıcıları Getir
```
GET /api/v1/users/active
```

## Access Data API Endpoints

### 1. Tüm Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata
```

**Query Parameters:**
- `page` (optional): Sayfa numarası (default: 0)
- `size` (optional): Sayfa boyutu (default: 50)
- `sortBy` (optional): Sıralama alanı (default: "id")
- `sortDir` (optional): Sıralama yönü "asc" veya "desc" (default: "desc")

**Örnek:**
```
GET /api/v1/accessdata?page=0&size=20&sortBy=tarih&sortDir=desc
```

### 2. ID'ye Göre Geçiş Kaydı Getir
```
GET /api/v1/accessdata/{id}
```

**Örnek:**
```
GET /api/v1/accessdata/789
```

### 3. Kart ID'ye Göre Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata/by-card/{kartId}
```

**Örnek:**
```
GET /api/v1/accessdata/by-card/CARD123456
```

### 4. Kullanıcı Kayıt Numarasına Göre Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata/by-user/{userKayitNo}
```

**Örnek:**
```
GET /api/v1/accessdata/by-user/123
```

### 5. Şubeye Göre Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata/by-branch/{branchId}
```

**Örnek:**
```
GET /api/v1/accessdata/by-branch/1
```

### 6. Tarih Aralığına Göre Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata/by-date-range
```

**Query Parameters:**
- `startDate`: Başlangıç tarihi (ISO format: YYYY-MM-DDTHH:mm:ss)
- `endDate`: Bitiş tarihi (ISO format: YYYY-MM-DDTHH:mm:ss)

**Örnek:**
```
GET /api/v1/accessdata/by-date-range?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
```

### 7. Geçiş Tipine Göre Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata/by-type/{gecisTipi}
```

**Query Parameters:**
- `startDate`: Başlangıç tarihi (ISO format: YYYY-MM-DDTHH:mm:ss)
- `endDate`: Bitiş tarihi (ISO format: YYYY-MM-DDTHH:mm:ss)

**Örnek:**
```
GET /api/v1/accessdata/by-type/Giriş?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
```

### 8. Belirli Tarihten Sonraki Geçiş Kayıtlarını Getir
```
GET /api/v1/accessdata/after-date
```

**Query Parameters:**
- `date`: Tarih (ISO format: YYYY-MM-DDTHH:mm:ss)

**Örnek:**
```
GET /api/v1/accessdata/after-date?date=2025-01-01T00:00:00
```

## Response Format

Tüm API endpoint'leri JSON formatında yanıt döner.

### Başarılı Yanıt (200 OK)
```json
{
  "id": 1,
  "adi": "Ahmet",
  "soyadi": "Yılmaz",
  "kartId": "CARD123456",
  ...
}
```

### Kayıt Bulunamadı (404 Not Found)
```
HTTP Status: 404 Not Found
```

### Sunucu Hatası (500 Internal Server Error)
```
HTTP Status: 500 Internal Server Error
```

## Kullanım Örnekleri

### cURL ile API Çağrıları

```bash
# Tüm kullanıcıları getir
curl -X GET "http://localhost:8080/api/v1/users"

# ID'ye göre kullanıcı getir
curl -X GET "http://localhost:8080/api/v1/users/123"

# Kart ID'ye göre kullanıcı getir
curl -X GET "http://localhost:8080/api/v1/users/by-card/CARD123456"

# Tarih aralığına göre geçiş kayıtları
curl -X GET "http://localhost:8080/api/v1/accessdata/by-date-range?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59"
```

### JavaScript ile API Çağrıları

```javascript
// Tüm kullanıcıları getir
fetch('/api/v1/users')
  .then(response => response.json())
  .then(data => console.log(data));

// ID'ye göre kullanıcı getir
fetch('/api/v1/users/123')
  .then(response => response.json())
  .then(data => console.log(data));

// Kart ID'ye göre geçiş kayıtları
fetch('/api/v1/accessdata/by-card/CARD123456')
  .then(response => response.json())
  .then(data => console.log(data));
```

## Notlar

- Tüm tarih parametreleri ISO 8601 formatında olmalıdır: `YYYY-MM-DDTHH:mm:ss`
- Sayfalama parametreleri opsiyoneldir ve belirtilmezse varsayılan değerler kullanılır
- API endpoint'leri CORS destekli olarak yapılandırılmıştır
- Hata durumlarında uygun HTTP status kodları döndürülür

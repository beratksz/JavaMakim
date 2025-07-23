package com.gecisystems.merkezi.service;

import com.gecisystems.merkezi.entity.Personel;
import com.gecisystems.merkezi.entity.GecisKaydi;
import com.gecisystems.merkezi.repository.PersonelRepository;
import com.gecisystems.merkezi.repository.GecisKaydiRepository;
import com.healthmarketscience.jackcess.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@EnableScheduling
public class SyncService {
    
    @Value("${access.database.path1}")
    private String accessDbPath1;
    
    @Value("${access.database.path2}")
    private String accessDbPath2;
    
    @Autowired
    private PersonelRepository personelRepository;
    
    @Autowired
    private GecisKaydiRepository gecisKaydiRepository;
    
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusHours(24);
    
    // Optimize edilmiş batch size - Hız ve güvenlik dengesi
    private static final int BATCH_SIZE = 100;
    
    // Her 5 dakikada bir senkronize et
    @Scheduled(fixedRate = 300000) // 5 dakika
    public void autoSync() {
        try {
            System.out.println("🔄 Otomatik senkronizasyon başlıyor...");
            Map<String, Object> result = syncAllData();
            System.out.println("✅ Otomatik senkronizasyon tamamlandı: " + result);
        } catch (Exception e) {
            System.err.println("❌ Otomatik senkronizasyon hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Manuel senkronizasyon - HYBRID APPROACH (Hızlı + Güvenli)
    public Map<String, Object> syncAllData() {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime syncStartTime = LocalDateTime.now();
        
        System.out.println("🚀 Hibrit senkronizasyon başlıyor... (Hızlı + Güvenli)");
        
        int personelAdded = 0;
        int gecisAdded = 0;
        
        try {
            // Şube 1 - Her şube paralel olarak işlenebilir
            System.out.println("📂 Şube 1 senkronizasyonu başlıyor...");
            personelAdded += syncPersonelFromAccessHybrid(accessDbPath1, 1);
            Thread.sleep(1000); // Kısa recovery
            gecisAdded += syncGecisFromAccessHybrid(accessDbPath1, 1);
            
            Thread.sleep(1000); // Şubeler arası kısa recovery
            
            // Şube 2 - Her şube paralel olarak işlenebilir  
            System.out.println("📂 Şube 2 senkronizasyonu başlıyor...");
            personelAdded += syncPersonelFromAccessHybrid(accessDbPath2, 2);
            Thread.sleep(1000); // Kısa recovery
            gecisAdded += syncGecisFromAccessHybrid(accessDbPath2, 2);
            
            result.put("status", "SUCCESS");
            result.put("personelAdded", personelAdded);
            result.put("gecisAdded", gecisAdded);
            result.put("syncTime", syncStartTime);
            result.put("syncMode", "HYBRID_FAST");
            
            long durationMs = java.time.Duration.between(syncStartTime, LocalDateTime.now()).toMillis();
            result.put("durationMs", durationMs);
            
            System.out.println("✅ Hibrit senkronizasyon tamamlandı! Süre: " + durationMs + "ms");
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            System.err.println("❌ Senkronizasyon hatası: " + e.getMessage());
            e.printStackTrace();
        }
        
        lastSyncTime = syncStartTime;
        return result;
    }
    
    // Hibrit personel senkronizasyonu - Hızlı batch + güvenli fallback
    private int syncPersonelFromAccessHybrid(String dbPath, int branchId) {
        int totalAdded = 0;
        
        try {
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                System.out.println("⚠️ Access DB bulunamadı: " + dbPath);
                return 0;
            }
            
            System.out.println("🔍 Personel kayıtları okunuyor... (Şube " + branchId + ")");
            
            List<Personel> allPersonel = new ArrayList<>();
            
            // Önce tüm Access verilerini oku
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table usersTable = db.getTable("Users");
                
                for (Row row : usersTable) {
                    Long accessId = getLongValue(row, "ID");
                    String kartId = getStringValue(row, "Kart ID");
                    String tcKimlik = getStringValue(row, "TCKimlik"); // Boş ama yine de oku
                    String adi = getStringValue(row, "Adi");
                    String soyadi = getStringValue(row, "Soyadi");
                    
                    // ID ve Kart ID'den en az biri dolu olmalı
                    if (adi == null || soyadi == null || (accessId == null && kartId == null)) continue;
                    
                    // DB'de var mı kontrol et - ID veya Kart ID ile
                    boolean exists = checkPersonelExists(accessId, kartId, tcKimlik);
                    
                    if (!exists) {
                        Personel personel = createPersonelFromRow(row);
                        allPersonel.add(personel);
                    }
                }
            }
            
            System.out.println("📝 " + allPersonel.size() + " yeni personel bulundu (Şube " + branchId + ")");
            
            // Büyük batch'ler halinde hızlı kaydet
            List<Personel> batch = new ArrayList<>();
            for (Personel personel : allPersonel) {
                batch.add(personel);
                
                if (batch.size() >= BATCH_SIZE) {
                    int saved = savePersonelBatchHybrid(batch, branchId);
                    totalAdded += saved;
                    batch.clear();
                    // Thread.sleep kaldırıldı - daha hızlı
                }
            }
            
            // Kalan kayıtları kaydet
            if (!batch.isEmpty()) {
                int saved = savePersonelBatchHybrid(batch, branchId);
                totalAdded += saved;
            }
            
            if (totalAdded > 0) {
                System.out.println("✅ Toplam " + totalAdded + " yeni personel eklendi (Şube " + branchId + ")");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Personel senkronizasyon hatası (Şube " + branchId + "): " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalAdded;
    }
    
    // Basit existence check - ID ve Kart ID ile kontrol (TC Kimlik boş)
    public boolean checkPersonelExists(Long accessId, String kartId, String tcKimlik) {
        try {
            if (accessId != null) {
                return personelRepository.existsByAccessId(accessId);
            } else if (kartId != null) {
                return personelRepository.existsByKartId(kartId);
            }
            // TC Kimlik boş olduğu için kontrol etmiyoruz
            return false;
        } catch (Exception e) {
            System.err.println("⚠️ Kontrol hatası: " + e.getMessage());
            return false;
        }
    }
    
    // Basit batch save - Transaction olmadan
    public int savePersonelBatchHybrid(List<Personel> batch, int branchId) {
        try {
            // Direct save - Transaction yönetimi Spring'e bırak
            List<Personel> saved = personelRepository.saveAll(batch);
            System.out.println("💾 " + saved.size() + " personel kaydedildi (Şube " + branchId + ") - Hızlı batch");
            return saved.size();
        } catch (Exception e) {
            System.err.println("⚠️ Batch save hatası, tek tek deneniyor (Şube " + branchId + "): " + e.getMessage());
            
            // Hata olursa tek tek kaydet
            int singleSaved = 0;
            for (Personel personel : batch) {
                try {
                    personelRepository.save(personel);
                    singleSaved++;
                } catch (Exception singleError) {
                    System.err.println("⚠️ Tek personel kaydı hatası: " + singleError.getMessage());
                }
            }
            System.out.println("💾 " + singleSaved + " personel kaydedildi (Şube " + branchId + ") - Fallback");
            return singleSaved;
        }
    }
    
    // Basit tek kayıt - Transaction olmadan (kullanılmıyor)
    public void savePersonelSingle(Personel personel) {
        personelRepository.save(personel);
    }
    
    // Hibrit geçiş senkronizasyonu - Hızlı batch + güvenli fallback
    private int syncGecisFromAccessHybrid(String dbPath, int branchId) {
        int totalAdded = 0;
        
        try {
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                System.out.println("⚠️ Access DB bulunamadı: " + dbPath);
                return 0;
            }
            
            System.out.println("🔍 Geçiş kayıtları okunuyor... (Şube " + branchId + ")");
            
            List<GecisKaydi> allGecis = new ArrayList<>();
            
            // Önce mevcut unique key'leri al - Tarih+Kart+Şube kombinasyonu
            Set<String> existingUniqueKeys = getExistingGecisUniqueKeys();
            
            // Tüm Access verilerini oku
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table accessDatasTable = db.getTable("AccessDatas");
                
                int totalRows = 0;
                
                for (Row row : accessDatasTable) {
                    totalRows++;
                    
                    Long kayitNo = getLongValue(row, "Kayit No");
                    if (kayitNo == null) continue;
                    
                    // Unique key oluştur: Tarih + Kart ID + Şube ID
                    LocalDateTime tarih = getDateTimeValue(row, "Tarih");
                    String kartId = getStringValue(row, "Kart ID");
                    
                    if (tarih == null || kartId == null) continue;
                    
                    String uniqueKey = tarih.toString() + ":" + kartId + ":" + branchId;
                    if (existingUniqueKeys.contains(uniqueKey)) continue;
                    
                    GecisKaydi gecis = createGecisFromRow(row, branchId);
                    allGecis.add(gecis);
                    existingUniqueKeys.add(uniqueKey); // Memory'de de işaretle - aynı session'da duplicate olmasın
                }
                
                System.out.println("📊 Total=" + totalRows + ", New=" + allGecis.size() + " (Şube " + branchId + ")");
            }
            
            // Küçük batch'ler halinde kaydet
            List<GecisKaydi> batch = new ArrayList<>();
            for (GecisKaydi gecis : allGecis) {
                batch.add(gecis);
                
                if (batch.size() >= BATCH_SIZE) {
                    int saved = saveGecisBatchHybrid(batch, branchId);
                    totalAdded += saved;
                    batch.clear();
                    // Thread.sleep kaldırıldı - daha hızlı
                }
            }
            
            // Kalan kayıtları kaydet
            if (!batch.isEmpty()) {
                int saved = saveGecisBatchHybrid(batch, branchId);
                totalAdded += saved;
            }
            
            if (totalAdded > 0) {
                System.out.println("✅ Toplam " + totalAdded + " yeni geçiş kaydı eklendi (Şube " + branchId + ")");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Geçiş senkronizasyon hatası (Şube " + branchId + "): " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalAdded;
    }
    
    // Mevcut geçiş kayıtlarının unique key'lerini al - Duplicate önlemek için
    public Set<String> getExistingGecisUniqueKeys() {
        try {
            Set<String> uniqueKeys = new HashSet<>();
            List<GecisKaydi> existing = gecisKaydiRepository.findAll();
            for (GecisKaydi g : existing) {
                // Unique key: Tarih + KartID + BranchId kombinasyonu
                if (g.getTarih() != null && g.getKartId() != null && g.getBranchId() != null) {
                    String uniqueKey = g.getTarih().toString() + ":" + g.getKartId() + ":" + g.getBranchId();
                    uniqueKeys.add(uniqueKey);
                }
            }
            return uniqueKeys;
        } catch (Exception e) {
            System.err.println("⚠️ Mevcut geçiş unique key'leri alınamadı: " + e.getMessage());
            return new HashSet<>();
        }
    }
    
    // Basit geçiş batch save - Transaction olmadan
    public int saveGecisBatchHybrid(List<GecisKaydi> batch, int branchId) {
        try {
            // Direct save - Transaction yönetimi Spring'e bırak
            List<GecisKaydi> saved = gecisKaydiRepository.saveAll(batch);
            System.out.println("💾 " + saved.size() + " geçiş kaydı kaydedildi (Şube " + branchId + ") - Hızlı batch");
            return saved.size();
        } catch (Exception e) {
            System.err.println("⚠️ Geçiş batch save hatası, tek tek deneniyor (Şube " + branchId + "): " + e.getMessage());
            
            // Hata olursa tek tek kaydet
            int singleSaved = 0;
            for (GecisKaydi gecis : batch) {
                try {
                    gecisKaydiRepository.save(gecis);
                    singleSaved++;
                } catch (Exception singleError) {
                    System.err.println("⚠️ Tek geçiş kaydı hatası: " + singleError.getMessage());
                }
            }
            System.out.println("💾 " + singleSaved + " geçiş kaydı kaydedildi (Şube " + branchId + ") - Fallback");
            return singleSaved;
        }
    }
    
    // Basit tek kayıt - Transaction olmadan (kullanılmıyor)
    public void saveGecisSingle(GecisKaydi gecis) {
        gecisKaydiRepository.save(gecis);
    }
    
    private Personel createPersonelFromRow(Row row) {
        Personel personel = new Personel();
        
        // Access'teki TÜM column'ları birebir eşle
        personel.setKayitNo(getLongValue(row, "Kayit No"));
        personel.setAccessId(getLongValue(row, "ID"));
        personel.setKartId(getStringValue(row, "Kart ID"));
        personel.setDogrulamaPin(getStringValue(row, "Dogrulama PIN"));
        personel.setKimlikPin(getStringValue(row, "Kimlik PIN"));
        personel.setAdi(getStringValue(row, "Adi"));
        personel.setSoyadi(getStringValue(row, "Soyadi"));
        personel.setKullaniciTipi(getIntegerValue(row, "Kullanici Tipi"));
        personel.setSifre(getStringValue(row, "Sifre"));
        personel.setGecisModu(getIntegerValue(row, "Gecis Modu"));
        personel.setGrupNo(getIntegerValue(row, "Grup No"));
        personel.setVisitorGrupNo(getIntegerValue(row, "Visitor Grup No"));
        personel.setResim(getBinaryValue(row, "Resim"));
        personel.setPlaka(getStringValue(row, "Plaka"));
        personel.setTcKimlik(getStringValue(row, "TCKimlik"));
        personel.setBlokNo(getIntegerValue(row, "Blok No"));
        personel.setDaire(getStringValue(row, "Daire"));
        personel.setAdres(getStringValue(row, "Adres"));
        personel.setGorev(getStringValue(row, "Gorev"));
        personel.setDepartmanNo(getIntegerValue(row, "Departman No"));
        personel.setSirketNo(getIntegerValue(row, "Sirket No"));
        personel.setAciklama(getStringValue(row, "Aciklama"));
        personel.setIptal(getBooleanValue(row, "Iptal"));
        personel.setGrupTakvimiAktif(getBooleanValue(row, "Grup Takvimi Aktif"));
        personel.setGrupTakvimiNo(getIntegerValue(row, "Grup Takvimi No"));
        personel.setSaat1(getIntegerValue(row, "Saat 1"));
        personel.setGrupNo1(getIntegerValue(row, "Grup No 1"));
        personel.setSaat2(getIntegerValue(row, "Saat 2"));
        personel.setGrupNo2(getIntegerValue(row, "Grup No 2"));
        personel.setSaat3(getIntegerValue(row, "Saat 3"));
        personel.setGrupNo3(getIntegerValue(row, "Grup No 3"));
        personel.setTmp(getStringValue(row, "Tmp"));
        personel.setSureliKullanici(getBooleanValue(row, "Sureli Kullanici"));
        personel.setBitisTarihi(getDateTimeValue(row, "Bitis Tarihi"));
        personel.setTelefon(getStringValue(row, "Telefon"));
        personel.setUcGrup(getIntegerValue(row, "3 Grup"));
        personel.setBitisSaati(getDateTimeValue(row, "Bitis Saati"));
        
        // Aktiflik durumu
        Boolean iptal = getBooleanValue(row, "Iptal");
        personel.setAktif(iptal == null || !iptal);
        
        return personel;
    }
    
    private GecisKaydi createGecisFromRow(Row row, int branchId) {
        GecisKaydi gecis = new GecisKaydi();
        
        // Access'teki TÜM column'ları birebir eşle
        gecis.setKayitNo(getLongValue(row, "Kayit No"));
        gecis.setAccessId(getLongValue(row, "ID"));
        gecis.setKartId(getStringValue(row, "Kart ID"));
        gecis.setTarih(getDateTimeValue(row, "Tarih"));
        gecis.setLokalBolgeNo(getIntegerValue(row, "Lokal Bolge No"));
        gecis.setGlobalBolgeNo(getIntegerValue(row, "Global Bolge No"));
        gecis.setPanelId(getIntegerValue(row, "Panel ID"));
        gecis.setKapiId(getIntegerValue(row, "Kapi ID"));
        gecis.setGecisTipi(getStringValue(row, "Gecis Tipi"));
        gecis.setKod(getStringValue(row, "Kod"));
        gecis.setKullaniciTipi(getIntegerValue(row, "Kullanici Tipi"));
        gecis.setVisitorKayitNo(getLongValue(row, "Visitor Kayit No"));
        gecis.setUserKayitNo(getLongValue(row, "User Kayit No"));
        gecis.setKontrol(getStringValue(row, "Kontrol"));
        gecis.setKontrolTarihi(getDateTimeValue(row, "Kontrol Tarihi"));
        gecis.setCanliResim(getBinaryValue(row, "Canli Resim"));
        gecis.setPlaka(getStringValue(row, "Plaka"));
        gecis.setKullaniciAdi(getStringValue(row, "Kullanici Adi"));
        gecis.setIslemVerisi1(getStringValue(row, "Islem Verisi 1"));
        gecis.setIslemVerisi2(getStringValue(row, "Islem Verisi 2"));
        
        // Şube bilgisi ekle
        gecis.setBranchId(branchId);
        
        return gecis;
    }
    
    // Tam veritabanı senkronizasyonu
    public Map<String, Object> syncFullDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Önce mevcut verileri temizle
            deleteAllDataSafe();
            
            // Tüm verileri senkronize et
            result = syncAllData();
            result.put("fullSync", true);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public void deleteAllDataSafe() {
        try {
            gecisKaydiRepository.deleteAll();
            personelRepository.deleteAll();
        } catch (Exception e) {
            System.err.println("❌ Veri temizleme hatası: " + e.getMessage());
        }
    }
    
    public Map<String, Object> getSyncStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            status.put("lastSyncTime", lastSyncTime);
            status.put("personelCount", getPersonelCountSafe());
            status.put("gecisCount", getGecisCountSafe());
            status.put("dbPath1", accessDbPath1);
            status.put("dbPath2", accessDbPath2);
        } catch (Exception e) {
            status.put("error", e.getMessage());
        }
        
        return status;
    }
    
    public long getPersonelCountSafe() {
        try {
            return personelRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public long getGecisCountSafe() {
        try {
            return gecisKaydiRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }
    
    // Helper methods - Access değerlerini güvenli şekilde al
    private String getStringValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            return value != null ? value.toString().trim() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Long getLongValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            if (value == null) return null;
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    private Integer getIntegerValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            if (value == null) return null;
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }
    
    private Boolean getBooleanValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            if (value == null) return null;
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            String str = value.toString().toLowerCase();
            return "true".equals(str) || "1".equals(str) || "yes".equals(str);
        } catch (Exception e) {
            return null;
        }
    }
    
    private LocalDateTime getDateTimeValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            if (value == null) return null;
            if (value instanceof Date) {
                return ((Date) value).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private byte[] getBinaryValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            if (value instanceof byte[]) {
                return (byte[]) value;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

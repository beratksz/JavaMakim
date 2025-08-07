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
import org.springframework.transaction.annotation.Transactional;

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
    
    // Batch size artık properties'ten okunacak
    @Value("${sync.batch.size:50}")
    private int batchSize;

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
    @Transactional
    public Map<String, Object> syncAllData() {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime syncStartTime = LocalDateTime.now();
        
        System.out.println("🚀 Hibrit senkronizasyon başlıyor... (Hızlı + Güvenli + Güncelleme)");
        
        int personelProcessed = 0; // Hem yeni hem güncellenen
        int gecisAdded = 0;
        
        try {
            // Şube 1 - Her şube paralel olarak işlenebilir
            System.out.println("📂 Şube 1 senkronizasyonu başlıyor...");
            personelProcessed += syncPersonelFromAccessHybrid(accessDbPath1, 1);
            Thread.sleep(2000); // Biraz daha uzun recovery
            gecisAdded += syncGecisFromAccessHybrid(accessDbPath1, 1);
            
            Thread.sleep(2000); // Şubeler arası daha uzun recovery
            
            // Şube 2 - Her şube paralel olarak işlenebilir  
            System.out.println("📂 Şube 2 senkronizasyonu başlıyor...");
            personelProcessed += syncPersonelFromAccessHybrid(accessDbPath2, 2);
            Thread.sleep(2000); // Biraz daha uzun recovery
            gecisAdded += syncGecisFromAccessHybrid(accessDbPath2, 2);
            
            result.put("status", "SUCCESS");
            result.put("personelProcessed", personelProcessed); // Eklenen + güncellenen
            result.put("gecisAdded", gecisAdded);
            result.put("syncTime", syncStartTime);
            result.put("syncMode", "HYBRID_FAST_WITH_UPDATE");
            
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
    
    // Hibrit personel senkronizasyonu - Hızlı batch + güvenli fallback + Güncelleme
    private int syncPersonelFromAccessHybrid(String dbPath, int branchId) {
        int totalProcessed = 0;
        int updatedCount = 0;
        int newCount = 0;
        
        try {
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                System.out.println("⚠️ Access DB bulunamadı: " + dbPath);
                return 0;
            }
            
            System.out.println("🔍 Personel kayıtları okunuyor ve güncelleniyor... (Şube " + branchId + ")");
            
            List<Personel> allPersonel = new ArrayList<>();
            
            // Önce tüm Access verilerini oku
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table usersTable = db.getTable("Users");
                
                System.out.println("📊 Access Users tablosu bulundu, kayıt sayısı kontrol ediliyor...");
                int rowCount = 0;
                
                for (Row row : usersTable) {
                    rowCount++;
                    if (rowCount % 100 == 0) {
                        System.out.println("📖 " + rowCount + " kayıt okundu...");
                    }
                    
                    Long accessId = getLongValue(row, "ID");
                    String kartId = getStringValue(row, "Kart ID");
                    String tcKimlik = getStringValue(row, "TCKimlik");
                    String adi = getStringValue(row, "Adi");
                    String soyadi = getStringValue(row, "Soyadi");
                    
                    // Debug bilgisi
                    if (rowCount <= 5) {
                        System.out.println("🔍 Kayıt " + rowCount + ": ID=" + accessId + ", KartID=" + kartId + ", Adi=" + adi + ", Soyadi=" + soyadi);
                    }
                    
                    // ID ve Kart ID'den en az biri dolu olmalı
                    if (adi == null || soyadi == null || (accessId == null && kartId == null)) {
                        System.out.println("⚠️ Eksik veri, kayıt atlandı: ID=" + accessId + ", KartID=" + kartId);
                        continue;
                    }
                    
                    // DB'de var mı kontrol et - güncelleme için mevcut kaydı al
                    Personel existingPersonel = checkAndGetExistingPersonel(accessId, kartId, tcKimlik);
                    
                    if (existingPersonel != null) {
                        // Mevcut personeli güncelle
                        boolean updated = updatePersonelFromRow(existingPersonel, row);
                        if (updated) {
                            updatedCount++;
                            totalProcessed++;
                        }
                    } else {
                        // Yeni personel ekle
                        Personel personel = createPersonelFromRow(row);
                        if (personel != null) {
                            personel.setBranchId(branchId); // Branch ID'yi ekle
                            allPersonel.add(personel);
                        }
                    }
                }
                
                System.out.println("📊 Toplam okunan kayıt: " + rowCount + " (Şube " + branchId + ")");
            }
            
            System.out.println("📝 " + allPersonel.size() + " yeni personel bulundu (Şube " + branchId + ")");
            
            // Büyük batch'ler halinde hızlı kaydet
            List<Personel> batch = new ArrayList<>();
            for (Personel personel : allPersonel) {
                batch.add(personel);
                if (batch.size() >= batchSize) {
                    int saved = savePersonelBatchHybrid(batch, branchId);
                    newCount += saved;
                    totalProcessed += saved;
                    batch.clear();
                    Thread.sleep(500); // Kısa bir bekleme ekle
                }
            }
            // Kalan kayıtları kaydet
            if (!batch.isEmpty()) {
                int saved = savePersonelBatchHybrid(batch, branchId);
                newCount += saved;
                totalProcessed += saved;
            }
            
            if (totalProcessed > 0) {
                System.out.println("✅ Toplam " + totalProcessed + " personel işlendi: " + 
                    newCount + " yeni, " + updatedCount + " güncellenen (Şube " + branchId + ")");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Personel senkronizasyon hatası (Şube " + branchId + "): " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalProcessed;
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
            Set<String> existingUniqueKeys = getExistingGecisUniqueKeys();
            int filteredCount = 0;
            
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table accessDatasTable = db.getTable("AccessDatas");
                
                System.out.println("📊 Access AccessDatas tablosu bulundu, kayıt sayısı kontrol ediliyor...");
                int totalRows = 0;
                int validRows = 0;
                int nullRows = 0;
                
                for (Row row : accessDatasTable) {
                    totalRows++;
                    
                    if (totalRows % 10000 == 0) {
                        System.out.println("📖 " + totalRows + " geçiş kaydı okundu... (Valid: " + validRows + ", Null: " + nullRows + ")");
                    }
                    
                    Long kayitNo = getLongValue(row, "Kayit No");
                    LocalDateTime tarih = getDateTimeValue(row, "Tarih");
                    String kartId = getStringValue(row, "Kart ID");
                    
                    // Debug için ilk 5 kayıt
                    if (totalRows <= 5) {
                        System.out.println("🔍 Geçiş " + totalRows + ": KayitNo=" + kayitNo + ", Tarih=" + tarih + ", KartID=" + kartId);
                    }
                    
                    if (kayitNo == null) {
                        nullRows++;
                        continue;
                    }
                    
                    if (tarih == null || kartId == null) {
                        nullRows++;
                        System.out.println("⚠️ Eksik geçiş verisi: Tarih=" + tarih + ", KartID=" + kartId);
                        continue;
                    }
                    
                    validRows++;
                    // String uniqueKey = tarih.toString() + ":" + kartId + ":" + branchId;
                    String uniqueKey = tarih.toString() + ":" + kartId;
                    
                    if (existingUniqueKeys.contains(uniqueKey)) {
                        filteredCount++;
                        continue;
                    }
                    
                    GecisKaydi gecis = createGecisFromRow(row, branchId);
                    if (gecis != null) {
                        allGecis.add(gecis);
                        existingUniqueKeys.add(uniqueKey);
                    }
                }
                
                System.out.println("📊 Access Geçiş Özeti (Şube " + branchId + "):");
                System.out.println("   - Toplam kayıt: " + totalRows);
                System.out.println("   - Geçerli kayıt: " + validRows);
                System.out.println("   - Null/Eksik: " + nullRows);
                System.out.println("   - Yeni kayıt: " + allGecis.size());
                System.out.println("   - Filtrelenen: " + filteredCount);
            }
            
            // Batch halinde kaydet
            List<GecisKaydi> batch = new ArrayList<>();
            for (GecisKaydi gecis : allGecis) {
                batch.add(gecis);
                if (batch.size() >= batchSize) {
                    int saved = saveGecisBatchHybrid(batch, branchId);
                    totalAdded += saved;
                    batch.clear();
                    Thread.sleep(100); // Kısa bekleme
                }
            }
            // Kalan kayıtları kaydet
            if (!batch.isEmpty()) {
                int saved = saveGecisBatchHybrid(batch, branchId);
                totalAdded += saved;
            }
            
            if (totalAdded > 0) {
                System.out.println("✅ Toplam " + totalAdded + " yeni geçiş kaydı eklendi (Şube " + branchId + ")");
            } else {
                System.out.println("⚠️ Hiç yeni geçiş kaydı eklenmedi (Şube " + branchId + ")");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Geçiş senkronizasyon hatası (Şube " + branchId + "): " + e.getMessage());
            e.printStackTrace();
        }
        
        return totalAdded;
    }
    
    // Personel var mı kontrol et ve güncelleme için döndür
    public Personel checkAndGetExistingPersonel(Long accessId, String kartId, String tcKimlik) {
        try {
            // Önce AccessId ile ara
            if (accessId != null) {
                Optional<Personel> existing = personelRepository.findByAccessId(accessId);
                if (existing.isPresent()) {
                    System.out.println("🔍 Personel bulundu (AccessID): " + accessId);
                    return existing.get();
                }
            }
            
            // Sonra KartId ile ara
            if (kartId != null && !kartId.trim().isEmpty()) {
                Optional<Personel> existing = personelRepository.findByKartId(kartId);
                if (existing.isPresent()) {
                    System.out.println("🔍 Personel bulundu (KartID): " + kartId);
                    return existing.get();
                }
            }
            
            // TC Kimlik ile de ara
            if (tcKimlik != null && !tcKimlik.trim().isEmpty()) {
                Optional<Personel> existing = personelRepository.findByTcKimlik(tcKimlik);
                if (existing.isPresent()) {
                    System.out.println("🔍 Personel bulundu (TC Kimlik): " + tcKimlik);
                    return existing.get();
                }
            }
            
            return null; // Bulunamadı
        } catch (Exception e) {
            System.err.println("⚠️ Personel arama hatası: " + e.getMessage());
            return null;
        }
    }

    // Eski basit existence check - geriye uyumluluk için
    public boolean checkPersonelExists(Long accessId, String kartId, String tcKimlik) {
        return checkAndGetExistingPersonel(accessId, kartId, tcKimlik) != null;
    }
    
    // Personel bilgilerini güncelle
    @Transactional
    public boolean updatePersonelFromRow(Personel existingPersonel, Row row) {
        try {
            System.out.println("🔄 Personel güncelleniyor: " + existingPersonel.getFullName());
            
            // Access'teki güncel bilgileri al ve mevcut personeli güncelle
            existingPersonel.setKayitNo(getLongValue(row, "Kayit No"));
            existingPersonel.setAccessId(getLongValue(row, "ID"));
            existingPersonel.setKartId(getStringValue(row, "Kart ID"));
            existingPersonel.setDogrulamaPin(getStringValue(row, "Dogrulama PIN"));
            existingPersonel.setKimlikPin(getStringValue(row, "Kimlik PIN"));
            existingPersonel.setAdi(getStringValue(row, "Adi"));
            existingPersonel.setSoyadi(getStringValue(row, "Soyadi"));
            existingPersonel.setKullaniciTipi(getIntegerValue(row, "Kullanici Tipi"));
            existingPersonel.setSifre(getStringValue(row, "Sifre"));
            existingPersonel.setGecisModu(getIntegerValue(row, "Gecis Modu"));
            existingPersonel.setGrupNo(getIntegerValue(row, "Grup No"));
            existingPersonel.setVisitorGrupNo(getIntegerValue(row, "Visitor Grup No"));
            existingPersonel.setResim(getBinaryValue(row, "Resim"));
            existingPersonel.setPlaka(getStringValue(row, "Plaka"));
            existingPersonel.setTcKimlik(getStringValue(row, "TCKimlik"));
            existingPersonel.setBlokNo(getIntegerValue(row, "Blok No"));
            existingPersonel.setDaire(getStringValue(row, "Daire"));
            existingPersonel.setAdres(getStringValue(row, "Adres"));
            existingPersonel.setGorev(getStringValue(row, "Gorev"));
            existingPersonel.setDepartmanNo(getIntegerValue(row, "Departman No"));
            existingPersonel.setSirketNo(getIntegerValue(row, "Sirket No"));
            existingPersonel.setAciklama(getStringValue(row, "Aciklama"));
            existingPersonel.setIptal(getBooleanValue(row, "Iptal"));
            existingPersonel.setGrupTakvimiAktif(getBooleanValue(row, "Grup Takvimi Aktif"));
            existingPersonel.setGrupTakvimiNo(getIntegerValue(row, "Grup Takvimi No"));
            existingPersonel.setSaat1(getIntegerValue(row, "Saat 1"));
            existingPersonel.setGrupNo1(getIntegerValue(row, "Grup No 1"));
            existingPersonel.setSaat2(getIntegerValue(row, "Saat 2"));
            existingPersonel.setGrupNo2(getIntegerValue(row, "Grup No 2"));
            existingPersonel.setSaat3(getIntegerValue(row, "Saat 3"));
            existingPersonel.setGrupNo3(getIntegerValue(row, "Grup No 3"));
            existingPersonel.setTmp(getStringValue(row, "Tmp"));
            existingPersonel.setSureliKullanici(getBooleanValue(row, "Sureli Kullanici"));
            existingPersonel.setBitisTarihi(getDateTimeValue(row, "Bitis Tarihi"));
            existingPersonel.setTelefon(getStringValue(row, "Telefon"));
            existingPersonel.setUcGrup(getIntegerValue(row, "3 Grup"));
            existingPersonel.setBitisSaati(getDateTimeValue(row, "Bitis Saati"));
            
            // Aktiflik durumu güncelle
            Boolean iptal = getBooleanValue(row, "Iptal");
            existingPersonel.setAktif(iptal == null || !iptal);
            
            // Updated_at otomatik güncellenecek (@PreUpdate)
            
            // Kaydet
            personelRepository.save(existingPersonel);
            System.out.println("✅ Personel güncellendi: " + existingPersonel.getFullName());
            return true;
            
        } catch (Exception e) {
            System.err.println("⚠️ Personel güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Mevcut geçiş kayıtlarının unique key'lerini al - DAHA BASIT
    public Set<String> getExistingGecisUniqueKeys() {
        try {
            long count = gecisKaydiRepository.count();
            System.out.println("ℹ️ Mevcut PostgreSQL geçiş kayıt sayısı: " + count);
            
            if (count == 0) {
                System.out.println("ℹ️ PostgreSQL'de hiç geçiş kaydı yok, tüm Access kayıtları eklenecek.");
                return new HashSet<>();
            }
            
            // Eğer çok fazla kayıt varsa, sadece son 3 günü kontrol et
            Set<String> uniqueKeys = new HashSet<>();
            
            List<GecisKaydi> existing = gecisKaydiRepository.findAll();
            System.out.println("ℹ️ Tüm mevcut kayıtlar kontrol ediliyor: " + existing.size());
            
            int debugCount = 0;
            for (GecisKaydi g : existing) {
                if (g.getTarih() != null && g.getKartId() != null) {
                    String uniqueKey = g.getTarih().toString() + ":" + g.getKartId();
                    uniqueKeys.add(uniqueKey);
                    if (debugCount < 20) {
                        System.out.println("[DEBUG] uniqueKey=" + uniqueKey + " | tarih=" + g.getTarih() + " | kartId=" + g.getKartId());
                        debugCount++;
                    }
                }
            }
            
            System.out.println("ℹ️ Unique key sayısı: " + uniqueKeys.size());
            return uniqueKeys;
            
        } catch (Exception e) {
            System.err.println("⚠️ Mevcut geçiş unique key'leri alınamadı: " + e.getMessage());
            e.printStackTrace();
            return new HashSet<>();
        }
    }
    
    // Basit personel batch save - Transaction'lı
    @Transactional
    public int savePersonelBatchHybrid(List<Personel> batch, int branchId) {
        try {
            System.out.println("💾 " + batch.size() + " personel kaydediliyor... (Şube " + branchId + ")");
            List<Personel> saved = personelRepository.saveAll(batch);
            System.out.println("✅ " + saved.size() + " personel başarıyla kaydedildi (Şube " + branchId + ")");
            return saved.size();
        } catch (Exception e) {
            System.err.println("⚠️ Personel batch save hatası, tek tek deneniyor (Şube " + branchId + "): " + e.getMessage());
            
            // Hata olursa tek tek kaydet
            int singleSaved = 0;
            for (Personel personel : batch) {
                try {
                    personelRepository.save(personel);
                    singleSaved++;
                } catch (Exception singleError) {
                    System.err.println("⚠️ Tek personel kaydı hatası (ID: " + personel.getAccessId() + "): " + singleError.getMessage());
                }
            }
            System.out.println("💾 " + singleSaved + " personel kaydedildi (Şube " + branchId + ") - Fallback");
            return singleSaved;
        }
    }
    
    // Basit geçiş batch save - Transaction'lı
    @Transactional
    public int saveGecisBatchHybrid(List<GecisKaydi> batch, int branchId) {
        try {
            System.out.println("💾 " + batch.size() + " geçiş kaydı kaydediliyor... (Şube " + branchId + ")");
            List<GecisKaydi> saved = gecisKaydiRepository.saveAll(batch);
            System.out.println("✅ " + saved.size() + " geçiş kaydı başarıyla kaydedildi (Şube " + branchId + ")");
            return saved.size();
        } catch (Exception e) {
            System.err.println("⚠️ Geçiş batch save hatası, tek tek deneniyor (Şube " + branchId + "): " + e.getMessage());
            e.printStackTrace();
            
            // Hata olursa tek tek kaydet
            int singleSaved = 0;
            for (GecisKaydi gecis : batch) {
                try {
                    gecisKaydiRepository.save(gecis);
                    singleSaved++;
                } catch (Exception singleError) {
                    System.err.println("⚠️ Tek geçiş kaydı hatası (KayitNo: " + gecis.getKayitNo() + "): " + singleError.getMessage());
                }
            }
            System.out.println("💾 " + singleSaved + " geçiş kaydı kaydedildi (Şube " + branchId + ") - Fallback");
            return singleSaved;
        }
    }
    
    private Personel createPersonelFromRow(Row row) {
        try {
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
            
        } catch (Exception e) {
            System.err.println("⚠️ Personel oluşturma hatası: " + e.getMessage());
            return null;
        }
    }
    
    private GecisKaydi createGecisFromRow(Row row, int branchId) {
        try {
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
            
            // Geçiş anındaki personel bilgilerini bul ve ekle
            String kartId = gecis.getKartId();
            if (kartId != null && !kartId.trim().isEmpty()) {
                Optional<Personel> personelOpt = personelRepository.findByKartId(kartId);
                if (personelOpt.isPresent()) {
                    Personel personel = personelOpt.get();
                    gecis.setGecisAnindakiAdi(personel.getAdi());
                    gecis.setGecisAnindakiSoyadi(personel.getSoyadi());
                    System.out.println("🔗 Geçiş anındaki personel bilgisi eklendi: " + 
                                     personel.getAdi() + " " + personel.getSoyadi() + " (Kart: " + kartId + ")");
                } else {
                    System.out.println("⚠️ Kart ID bulunamadı: " + kartId);
                }
            }
            
            return gecis;
            
        } catch (Exception e) {
            System.err.println("⚠️ Geçiş kaydı oluşturma hatası: " + e.getMessage());
            return null;
        }
    }
    
    // Test metodu - Sadece birkaç kayıt oku
    public Map<String, Object> testAccessConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Şube 1 test
            File dbFile1 = new File(accessDbPath1);
            if (dbFile1.exists()) {
                try (Database db = DatabaseBuilder.open(dbFile1)) {
                    Table usersTable = db.getTable("Users");
                    Table accessDatasTable = db.getTable("AccessDatas");
                    
                    result.put("db1_users_count", usersTable.getRowCount());
                    result.put("db1_access_count", accessDatasTable.getRowCount());
                    result.put("db1_status", "OK");
                }
            } else {
                result.put("db1_status", "FILE_NOT_FOUND");
            }
            
            // Şube 2 test
            File dbFile2 = new File(accessDbPath2);
            if (dbFile2.exists()) {
                try (Database db = DatabaseBuilder.open(dbFile2)) {
                    Table usersTable = db.getTable("Users");
                    Table accessDatasTable = db.getTable("AccessDatas");
                    
                    result.put("db2_users_count", usersTable.getRowCount());
                    result.put("db2_access_count", accessDatasTable.getRowCount());
                    result.put("db2_status", "OK");
                }
            } else {
                result.put("db2_status", "FILE_NOT_FOUND");
            }
            
            result.put("test_status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("test_status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    // Tam veritabanı senkronizasyonu
    @Transactional
    public Map<String, Object> syncFullDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("🗑️ Mevcut veriler temizleniyor...");
            // Önce mevcut verileri temizle
            deleteAllDataSafe();
            
            System.out.println("🔄 Full senkronizasyon başlıyor...");
            // Tüm verileri senkronize et
            result = syncAllData();
            result.put("fullSync", true);
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Transactional
    public void deleteAllDataSafe() {
        try {
            long gecisCount = gecisKaydiRepository.count();
            long personelCount = personelRepository.count();
            
            System.out.println("🗑️ Silinen geçiş kayıtları: " + gecisCount);
            System.out.println("🗑️ Silinen personel kayıtları: " + personelCount);
            
            gecisKaydiRepository.deleteAll();
            personelRepository.deleteAll();
            
            System.out.println("✅ Tüm veriler temizlendi");
        } catch (Exception e) {
            System.err.println("❌ Veri temizleme hatası: " + e.getMessage());
            e.printStackTrace();
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
            if (value == null) return null;
            String strValue = value.toString().trim();
            return strValue.isEmpty() ? null : strValue;
        } catch (Exception e) {
            System.err.println("⚠️ String değer alma hatası (" + columnName + "): " + e.getMessage());
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
            String strValue = value.toString().trim();
            if (strValue.isEmpty()) return null;
            return Long.parseLong(strValue);
        } catch (Exception e) {
            System.err.println("⚠️ Long değer alma hatası (" + columnName + "): " + e.getMessage());
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
            String strValue = value.toString().trim();
            if (strValue.isEmpty()) return null;
            return Integer.parseInt(strValue);
        } catch (Exception e) {
            System.err.println("⚠️ Integer değer alma hatası (" + columnName + "): " + e.getMessage());
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
            if (value instanceof Number) {
                return ((Number) value).intValue() != 0;
            }
            String str = value.toString().toLowerCase().trim();
            return "true".equals(str) || "1".equals(str) || "yes".equals(str) || "evet".equals(str);
        } catch (Exception e) {
            System.err.println("⚠️ Boolean değer alma hatası (" + columnName + "): " + e.getMessage());
            return null;
        }
    }
    
private LocalDateTime getDateTimeValue(Row row, String columnName) {
    try {
        Object value = row.get(columnName);
        if (value == null) return null;
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Date) {
            return ((Date) value).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        // Eğer başka bir tip gelirse, burada log ile gösterilecek
        return null;
    } catch (Exception e) {
        System.err.println("⚠️ DateTime değer alma hatası (" + columnName + "): " + e.getMessage());
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
            System.err.println("⚠️ Binary değer alma hatası (" + columnName + "): " + e.getMessage());
            return null;
        }
    }
    
    // Debug metodu - İlk 10 geçiş kaydını oku ve yazdır
    public Map<String, Object> debugAccessData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            File dbFile = new File(accessDbPath1);
            if (!dbFile.exists()) {
                result.put("error", "Access DB dosyası bulunamadı: " + accessDbPath1);
                return result;
            }
            
            List<Map<String, Object>> sampleRecords = new ArrayList<>();
            
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table accessDatasTable = db.getTable("AccessDatas");
                
                System.out.println("🔍 AccessDatas tablosu bulundu");
                System.out.println("📊 Toplam kayıt sayısı: " + accessDatasTable.getRowCount());
                
                // İlk 10 kaydı oku
                int count = 0;
                for (Row row : accessDatasTable) {
                    if (count >= 10) break;
                    
                    Map<String, Object> record = new HashMap<>();
                    record.put("kayitNo", getLongValue(row, "Kayit No"));
                    record.put("id", getLongValue(row, "ID"));
                    record.put("kartId", getStringValue(row, "Kart ID"));
                    record.put("tarih", getDateTimeValue(row, "Tarih"));
                    record.put("lokalBolgeNo", getIntegerValue(row, "Lokal Bolge No"));
                    record.put("gecisTipi", getStringValue(row, "Gecis Tipi"));
                    
                    sampleRecords.add(record);
                    count++;
                }
                
                result.put("totalRecords", accessDatasTable.getRowCount());
                result.put("sampleRecords", sampleRecords);
                result.put("status", "SUCCESS");
                
                // Tablo sütunlarını da listele
                List<String> columns = new ArrayList<>();
                for (Column col : accessDatasTable.getColumns()) {
                    columns.add(col.getName());
                }
                result.put("columns", columns);
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    // Manuel tek kayıt test metodu
    public Map<String, Object> testSingleRecord() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            File dbFile = new File(accessDbPath1);
            if (!dbFile.exists()) {
                result.put("error", "Access DB dosyası bulunamadı");
                return result;
            }
            
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table accessDatasTable = db.getTable("AccessDatas");
                
                // İlk geçerli kaydı bul
                for (Row row : accessDatasTable) {
                    Long kayitNo = getLongValue(row, "Kayit No");
                    LocalDateTime tarih = getDateTimeValue(row, "Tarih");
                    String kartId = getStringValue(row, "Kart ID");
                    
                    if (kayitNo != null && tarih != null && kartId != null) {
                        // Bu kaydı PostgreSQL'e kaydetmeyi dene
                        GecisKaydi gecis = createGecisFromRow(row, 999); // Test branch ID
                        
                        if (gecis != null) {
                            try {
                                GecisKaydi saved = gecisKaydiRepository.save(gecis);
                                result.put("status", "SUCCESS");
                                result.put("savedRecord", saved);
                                result.put("message", "Tek kayıt başarıyla kaydedildi");
                                break;
                            } catch (Exception saveError) {
                                result.put("status", "SAVE_ERROR");
                                result.put("error", saveError.getMessage());
                                result.put("gecisData", gecis);
                                break;
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
}
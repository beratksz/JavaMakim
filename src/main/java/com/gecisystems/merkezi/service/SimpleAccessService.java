package com.gecisystems.merkezi.service;

import com.gecisystems.merkezi.entity.Personel;
import com.gecisystems.merkezi.repository.PersonelRepository;
import com.healthmarketscience.jackcess.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class SimpleAccessService {
    
    @Value("${access.database.path1:C:\\DB\\DB1.mdb}")
    private String accessDbPath;
    
    @Autowired
    private PersonelRepository personelRepository;
    
    // Mevcut personel listesi (PostgreSQL'den)
    public List<Map<String, Object>> getPersonelList() {
        List<Map<String, Object>> personelList = new ArrayList<>();
        
        var personeller = personelRepository.findAll();
        for (var personel : personeller) {
            Map<String, Object> p = new HashMap<>();
            p.put("id", personel.getId());
            // Access'teki TÜM 36 column - EKSİKSİZ
            p.put("Kayit No", personel.getKayitNo());
            p.put("ID", personel.getAccessId());
            p.put("Kart ID", personel.getKartId());
            p.put("Dogrulama PIN", personel.getDogrulamaPin());
            p.put("Kimlik PIN", personel.getKimlikPin());
            p.put("Adi", personel.getAdi());
            p.put("Soyadi", personel.getSoyadi());
            p.put("Kullanici Tipi", personel.getKullaniciTipi());
            p.put("Sifre", personel.getSifre());
            p.put("Gecis Modu", personel.getGecisModu());
            p.put("Grup No", personel.getGrupNo());
            p.put("Visitor Grup No", personel.getVisitorGrupNo());
            p.put("Resim", personel.getResim());
            p.put("Plaka", personel.getPlaka());
            p.put("TCKimlik", personel.getTcKimlik());
            p.put("Blok No", personel.getBlokNo());
            p.put("Daire", personel.getDaire());
            p.put("Adres", personel.getAdres());
            p.put("Gorev", personel.getGorev());
            p.put("Departman No", personel.getDepartmanNo());
            p.put("Sirket No", personel.getSirketNo());
            p.put("Aciklama", personel.getAciklama());
            p.put("Iptal", personel.getIptal());
            p.put("Grup Takvimi Aktif", personel.getGrupTakvimiAktif());
            p.put("Grup Takvimi No", personel.getGrupTakvimiNo());
            p.put("Saat 1", personel.getSaat1());
            p.put("Grup No 1", personel.getGrupNo1());
            p.put("Saat 2", personel.getSaat2());
            p.put("Grup No 2", personel.getGrupNo2());
            p.put("Saat 3", personel.getSaat3());
            p.put("Grup No 3", personel.getGrupNo3());
            p.put("Tmp", personel.getTmp());
            p.put("Sureli Kullanici", personel.getSureliKullanici());
            p.put("Bitis Tarihi", personel.getBitisTarihi());
            p.put("Telefon", personel.getTelefon());
            p.put("3 Grup", personel.getUcGrup());
            p.put("Bitis Saati", personel.getBitisSaati());
            // Ekstra alan
            p.put("aktif", personel.getAktif());
            personelList.add(p);
        }
        
        return personelList;
    }
    
    // Şubeye göre personel listesi (PostgreSQL'den)
    public List<Map<String, Object>> getPersonelListBySube(int subeNo) {
        List<Personel> personelList = personelRepository.findBySubeNo(subeNo);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Personel personel : personelList) {
            Map<String, Object> p = new HashMap<>();
            p.put("id", personel.getId());
            // Access'teki TÜM 36 column - EKSİKSİZ
            p.put("Kayit No", personel.getKayitNo());
            p.put("ID", personel.getAccessId());
            p.put("Kart ID", personel.getKartId());
            p.put("Dogrulama PIN", personel.getDogrulamaPin());
            p.put("Kimlik PIN", personel.getKimlikPin());
            p.put("Adi", personel.getAdi());
            p.put("Soyadi", personel.getSoyadi());
            p.put("Kullanici Tipi", personel.getKullaniciTipi());
            p.put("Sifre", personel.getSifre());
            p.put("Gecis Modu", personel.getGecisModu());
            p.put("Grup No", personel.getGrupNo());
            p.put("Visitor Grup No", personel.getVisitorGrupNo());
            p.put("Resim", personel.getResim());
            p.put("Plaka", personel.getPlaka());
            p.put("TCKimlik", personel.getTcKimlik());
            p.put("Blok No", personel.getBlokNo());
            p.put("Daire", personel.getDaire());
            p.put("Adres", personel.getAdres());
            p.put("Gorev", personel.getGorev());
            p.put("Departman No", personel.getDepartmanNo());
            p.put("Sirket No", personel.getSirketNo());
            p.put("Aciklama", personel.getAciklama());
            p.put("Iptal", personel.getIptal());
            p.put("Grup Takvimi Aktif", personel.getGrupTakvimiAktif());
            p.put("Grup Takvimi No", personel.getGrupTakvimiNo());
            p.put("Saat 1", personel.getSaat1());
            p.put("Grup No 1", personel.getGrupNo1());
            p.put("Saat 2", personel.getSaat2());
            p.put("Grup No 2", personel.getGrupNo2());
            p.put("Saat 3", personel.getSaat3());
            p.put("Grup No 3", personel.getGrupNo3());
            p.put("Tmp", personel.getTmp());
            p.put("Sureli Kullanici", personel.getSureliKullanici());
            p.put("Bitis Tarihi", personel.getBitisTarihi());
            p.put("Telefon", personel.getTelefon());
            p.put("3 Grup", personel.getUcGrup());
            p.put("Bitis Saati", personel.getBitisSaati());
            // Ekstra alanlar
            p.put("aktif", personel.getAktif());
            p.put("subeNo", personel.getSubeNo());
            result.add(p);
        }
        
        return result;
    }
    
    // Geçiş kayıtları - direkt Access DB'den oku
    public List<Map<String, Object>> getGecisKayitlari(int limit, String kartId, String startDate, String endDate) {
        List<Map<String, Object>> gecisler = new ArrayList<>();
        
        try {
            File dbFile = new File(accessDbPath);
            
            try (Database db = DatabaseBuilder.open(dbFile)) {
                Table accessDatasTable = db.getTable("AccessDatas");
                Table usersTable = db.getTable("Users");
                
                // Users tablosunu Map'e al (hızlı erişim için)
                Map<Long, Map<String, Object>> usersMap = new HashMap<>();
                for (Row userRow : usersTable) {
                    Long kayitNo = getLongValue(userRow, "Kayit No");
                    if (kayitNo != null) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("kayitNo", kayitNo);
                        user.put("id", getLongValue(userRow, "ID"));
                        user.put("kartId", getStringValue(userRow, "Kart ID"));
                        user.put("adi", getStringValue(userRow, "Adi"));
                        user.put("soyadi", getStringValue(userRow, "Soyadi"));
                        usersMap.put(kayitNo, user);
                    }
                }
                
                int count = 0;
                for (Row row : accessDatasTable) {
                    if (count >= limit) break;
                    
                    // Tarih kontrolü - boş olanları atla
                    Date tarih = getDateValue(row, "Tarih");
                    if (tarih == null) continue;
                    
                    // ID = 0 olanları atla
                    Long accessId = getLongValue(row, "ID");
                    if (accessId == null || accessId == 0) continue;
                    
                    // Kart ID filtresi
                    String rowKartId = getStringValue(row, "Kart ID");
                    if (kartId != null && !kartId.equals(rowKartId)) continue;
                    
                    // Tarih aralığı filtresi
                    if (startDate != null || endDate != null) {
                        LocalDateTime gecisZamani = tarih.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        if (startDate != null) {
                            LocalDateTime baslangic = LocalDateTime.parse(startDate + "T00:00:00");
                            if (gecisZamani.isBefore(baslangic)) continue;
                        }
                        if (endDate != null) {
                            LocalDateTime bitis = LocalDateTime.parse(endDate + "T23:59:59");
                            if (gecisZamani.isAfter(bitis)) continue;
                        }
                    }
                    
                    // User bilgisini al
                    Long userKayitNo = getLongValue(row, "User Kayit No");
                    Map<String, Object> userInfo = usersMap.get(userKayitNo);
                    
                    Map<String, Object> gecis = new HashMap<>();
                    // Access'teki TÜM 20 column - EKSİKSİZ
                    gecis.put("Kayit No", getLongValue(row, "Kayit No"));
                    gecis.put("ID", accessId);
                    gecis.put("Kart ID", rowKartId);
                    gecis.put("Tarih", tarih.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    gecis.put("Lokal Bolge No", getLongValue(row, "Lokal Bolge No"));
                    gecis.put("Global Bolge No", getLongValue(row, "Global Bolge No"));
                    gecis.put("Panel ID", getLongValue(row, "Panel ID"));
                    gecis.put("Kapi ID", getLongValue(row, "Kapi ID"));
                    gecis.put("Gecis Tipi", getStringValue(row, "Gecis Tipi"));
                    gecis.put("Kod", getStringValue(row, "Kod"));
                    gecis.put("Kullanici Tipi", getLongValue(row, "Kullanici Tipi"));
                    gecis.put("Visitor Kayit No", getLongValue(row, "Visitor Kayit No"));
                    gecis.put("User Kayit No", userKayitNo);
                    gecis.put("Kontrol", getStringValue(row, "Kontrol"));
                    gecis.put("Kontrol Tarihi", getDateValue(row, "Kontrol Tarihi"));
                    gecis.put("Canli Resim", getBinaryValue(row, "Canli Resim"));
                    gecis.put("Plaka", getStringValue(row, "Plaka"));
                    gecis.put("Kullanici Adi", getStringValue(row, "Kullanici Adi"));
                    gecis.put("Islem Verisi 1", getStringValue(row, "Islem Verisi 1"));
                    gecis.put("Islem Verisi 2", getStringValue(row, "Islem Verisi 2"));
                    
                    // User bilgilerini ekle (Access Users tablosundan)
                    if (userInfo != null) {
                        gecis.put("User_ID", userInfo.get("id"));
                        gecis.put("User_Adi", userInfo.get("adi"));
                        gecis.put("User_Soyadi", userInfo.get("soyadi"));
                        gecis.put("User_Kart_ID", userInfo.get("kartId"));
                    }
                    
                    gecisler.add(gecis);
                    count++;
                }
            }
        } catch (Exception e) {
            System.err.println("Geçiş kayıtları okunurken hata: " + e.getMessage());
        }
        
        return gecisler;
    }
    
    // Backward compatibility
    public List<Map<String, Object>> getGecisKayitlari(int limit, String kartId) {
        return getGecisKayitlari(limit, kartId, null, null);
    }
    
    // Sistem durumu
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // PostgreSQL personel sayısı
            long personelCount = personelRepository.count();
            status.put("personelCount", personelCount);
            
            // Access DB bağlantı kontrolü
            File dbFile = new File(accessDbPath);
            if (!dbFile.exists()) {
                status.put("accessDb", "DOSYA_YOK");
                status.put("accessDbPath", accessDbPath);
            } else {
                try (Database db = DatabaseBuilder.open(dbFile)) {
                    // Access DB'deki kayıt sayıları
                    Table usersTable = db.getTable("Users");
                    int usersCount = 0;
                    for (@SuppressWarnings("unused") Row row : usersTable) usersCount++;
                    
                    Table accessDatasTable = db.getTable("AccessDatas");
                    int accessCount = 0;
                    for (@SuppressWarnings("unused") Row row : accessDatasTable) {
                        accessCount++;
                        if (accessCount >= 1000) break; // İlk 1000'i say
                    }
                    
                    status.put("accessDb", "OK");
                    status.put("accessDbPath", accessDbPath);
                    status.put("accessUsersCount", usersCount);
                    status.put("accessDatasSample", accessCount >= 1000 ? "1000+" : accessCount);
                    
                } catch (Exception e) {
                    status.put("accessDb", "HATA");
                    status.put("accessDbError", e.getMessage());
                }
            }
            
            status.put("status", "OK");
            status.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", e.getMessage());
        }
        
        return status;
    }
    
    // Helper methods
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
    
    private Date getDateValue(Row row, String columnName) {
        try {
            Object value = row.get(columnName);
            if (value instanceof Date) {
                return (Date) value;
            }
            if (value instanceof java.time.LocalDateTime) {
                return Date.from(((java.time.LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
            }
            if (value instanceof java.sql.Timestamp) {
                return new Date(((java.sql.Timestamp) value).getTime());
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

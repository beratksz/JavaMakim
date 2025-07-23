package com.gecisystems.merkezi.service;

import com.gecisystems.merkezi.repository.PersonelRepository;
import com.gecisystems.merkezi.repository.GecisKaydiRepository;
import com.healthmarketscience.jackcess.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class DataComparisonService {
    
    @Value("${access.database.path1}")
    private String accessDbPath1;
    
    @Autowired
    private PersonelRepository personelRepository;
    
    @Autowired
    private GecisKaydiRepository gecisKaydiRepository;
    
    // PostgreSQL ve Access verilerini karşılaştır
    public Map<String, Object> compareData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // PostgreSQL sayıları
            long postgresPersonelCount = personelRepository.count();
            long postgresGecisCount = gecisKaydiRepository.count();
            
            result.put("postgresPersonelCount", postgresPersonelCount);
            result.put("postgresGecisCount", postgresGecisCount);
            
            // Access sayıları
            Map<String, Object> accessCounts = getAccessCounts();
            result.putAll(accessCounts);
            
            // Karşılaştırma
            long accessUsersCount = (Long) accessCounts.getOrDefault("accessUsersCount", 0L);
            long accessDataCount = (Long) accessCounts.getOrDefault("accessDataCount", 0L);
            
            result.put("personelFark", accessUsersCount - postgresPersonelCount);
            result.put("gecisFark", accessDataCount - postgresGecisCount);
            
            // Durum
            boolean personelEsit = accessUsersCount == postgresPersonelCount;
            boolean gecisEsit = accessDataCount == postgresGecisCount;
            
            result.put("personelEsit", personelEsit);
            result.put("gecisEsit", gecisEsit);
            result.put("tumEsit", personelEsit && gecisEsit);
            
            System.out.println("📊 VERİ KARŞILAŞTIRMASI:");
            System.out.println("👥 Personel - Access: " + accessUsersCount + ", PostgreSQL: " + postgresPersonelCount + " (Fark: " + (accessUsersCount - postgresPersonelCount) + ")");
            System.out.println("🚪 Geçiş - Access: " + accessDataCount + ", PostgreSQL: " + postgresGecisCount + " (Fark: " + (accessDataCount - postgresGecisCount) + ")");
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println("❌ Veri karşılaştırma hatası: " + e.getMessage());
        }
        
        return result;
    }
    
    private Map<String, Object> getAccessCounts() {
        Map<String, Object> counts = new HashMap<>();
        
        try {
            File dbFile = new File(accessDbPath1);
            if (!dbFile.exists()) {
                counts.put("error", "Access DB bulunamadı: " + accessDbPath1);
                return counts;
            }
            
            try (Database db = DatabaseBuilder.open(dbFile)) {
                // Users sayısı
                Table usersTable = db.getTable("Users");
                int usersCount = 0;
                for (@SuppressWarnings("unused") Row row : usersTable) {
                    usersCount++;
                }
                counts.put("accessUsersCount", (long) usersCount);
                
                // AccessDatas sayısı  
                Table accessDatasTable = db.getTable("AccessDatas");
                int accessDataCount = 0;
                for (@SuppressWarnings("unused") Row row : accessDatasTable) {
                    accessDataCount++;
                }
                counts.put("accessDataCount", (long) accessDataCount);
                
                // Aktif kullanıcı sayısı (Iptal != true)
                int activeUsersCount = 0;
                for (Row row : usersTable) {
                    Boolean iptal = getBooleanValue(row, "Iptal");
                    if (iptal == null || !iptal) {
                        activeUsersCount++;
                    }
                }
                counts.put("accessActiveUsersCount", (long) activeUsersCount);
                
                // ID=0 olmayan geçiş sayısı
                int validAccessDataCount = 0;
                for (Row row : accessDatasTable) {
                    Long id = getLongValue(row, "ID");
                    if (id != null && id != 0) {
                        validAccessDataCount++;
                    }
                }
                counts.put("accessValidDataCount", (long) validAccessDataCount);
                
            }
        } catch (Exception e) {
            counts.put("error", e.getMessage());
        }
        
        return counts;
    }
    
    // Helper methods
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
            return "true".equals(str) || "1".equals(str) || "yes".equals(str);
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
}

package com.gecisystems.merkezi.service;

import com.healthmarketscience.jackcess.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class AccessColumnDebugService {
    
    @Value("${access.database.path1}")
    private String accessDbPath1;
    
    // Access'teki tüm kolon isimlerini öğren
    public Map<String, Object> getAccessTableColumns() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            File dbFile = new File(accessDbPath1);
            if (!dbFile.exists()) {
                result.put("error", "Access DB bulunamadı: " + accessDbPath1);
                return result;
            }
            
            try (Database db = DatabaseBuilder.open(dbFile)) {
                // Users tablosu kolonları
                Table usersTable = db.getTable("Users");
                List<String> usersColumns = new ArrayList<>();
                for (Column column : usersTable.getColumns()) {
                    usersColumns.add(column.getName());
                }
                result.put("usersColumns", usersColumns);
                
                // AccessDatas tablosu kolonları
                Table accessDatasTable = db.getTable("AccessDatas");
                List<String> accessDatasColumns = new ArrayList<>();
                for (Column column : accessDatasTable.getColumns()) {
                    accessDatasColumns.add(column.getName());
                }
                result.put("accessDatasColumns", accessDatasColumns);
                
                // Tüm tablo isimleri
                Set<String> tableNames = db.getTableNames();
                result.put("allTables", tableNames);
                
                System.out.println("🔍 ACCESS VERİTABANI KOLON ANALİZİ:");
                System.out.println("📋 Users tablosu kolonları: " + usersColumns);
                System.out.println("📋 AccessDatas tablosu kolonları: " + accessDatasColumns);
                System.out.println("📋 Tüm tablolar: " + tableNames);
                
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
            System.err.println("❌ Access kolon analizi hatası: " + e.getMessage());
        }
        
        return result;
    }
}

package com.gecisystems.merkezi.controller;

import com.gecisystems.merkezi.service.SimpleAccessService;
import com.gecisystems.merkezi.service.AccessColumnDebugService;
import com.gecisystems.merkezi.service.DataComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class SimpleAccessController {
    
    @Autowired
    private SimpleAccessService simpleAccessService;
    
    @Autowired
    private AccessColumnDebugService accessColumnDebugService;
    
    @Autowired
    private DataComparisonService dataComparisonService;
    
    // Ana sayfa
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    // Personel listesi (PostgreSQL'den)
    @GetMapping("/api/simple/personel")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPersonel() {
        try {
            List<Map<String, Object>> personel = simpleAccessService.getPersonelList();
            return ResponseEntity.ok(personel);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Personel listesi (Şubeye göre PostgreSQL'den)
    @GetMapping("/api/simple/personel/sube/{subeNo}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPersonelBySube(@PathVariable int subeNo) {
        try {
            List<Map<String, Object>> personel = simpleAccessService.getPersonelListBySube(subeNo);
            return ResponseEntity.ok(personel);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Geçiş kayıtları - direkt Access DB'den (eski yöntem)
    @GetMapping("/api/simple/gecis")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getGecisKayitlari(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String kartId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            List<Map<String, Object>> gecisler = simpleAccessService.getGecisKayitlari(limit, kartId, startDate, endDate);
            return ResponseEntity.ok(gecisler);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // Sistem durumu
    @GetMapping("/api/simple/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = simpleAccessService.getStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    // === SENKRONIZASYON API'LERİ ===
    
    // Access kolon analizi (DEBUG)
    @GetMapping("/api/debug/columns")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAccessColumns() {
        try {
            Map<String, Object> result = accessColumnDebugService.getAccessTableColumns();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", "ERROR",
                "error", e.getMessage()
            );
            return ResponseEntity.status(500).body(error);
        }
    }
    
    // Veri karşılaştırması (Access vs PostgreSQL)
    @GetMapping("/api/debug/compare")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> compareData() {
        try {
            Map<String, Object> result = dataComparisonService.compareData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = Map.of(
                "status", "ERROR",
                "error", e.getMessage()
            );
            return ResponseEntity.status(500).body(error);
        }
    }
}

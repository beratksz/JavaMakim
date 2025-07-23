package com.gecisystems.merkezi.controller;

import com.gecisystems.merkezi.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")
public class SyncController {
    
    @Autowired
    private SyncService syncService;
    
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> syncAll() {
        try {
            Map<String, Object> result = syncService.syncAllData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "ERROR",
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/full")
    public ResponseEntity<Map<String, Object>> syncFull() {
        try {
            Map<String, Object> result = syncService.syncFullDatabase();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "ERROR", 
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = syncService.getSyncStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "ERROR",
                "error", e.getMessage()
            ));
        }
    }
}

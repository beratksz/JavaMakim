package com.gecisystems.merkezi.controller;

import com.gecisystems.merkezi.entity.Personel;
import com.gecisystems.merkezi.entity.GecisKaydi;
import com.gecisystems.merkezi.repository.PersonelRepository;
import com.gecisystems.merkezi.repository.GecisKaydiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    
    @Autowired
    private PersonelRepository personelRepository;
    
    @Autowired
    private GecisKaydiRepository gecisKaydiRepository;
    
    // ===== USERS API ENDPOINTS =====
    
    /**
     * Tüm kullanıcıları getir
     * GET /api/v1/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<Personel>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Personel> userPage = personelRepository.findAll(pageable);
            
            return ResponseEntity.ok(userPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * ID'ye göre kullanıcı getir
     * GET /api/v1/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<Personel> getUserById(@PathVariable Long id) {
        try {
            Optional<Personel> user = personelRepository.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Access ID'ye göre kullanıcı getir
     * GET /api/v1/users/by-access-id/{accessId}
     */
    @GetMapping("/users/by-access-id/{accessId}")
    public ResponseEntity<Personel> getUserByAccessId(@PathVariable Long accessId) {
        try {
            Optional<Personel> user = personelRepository.findByAccessId(accessId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Kart ID'ye göre kullanıcı getir
     * GET /api/v1/users/by-card/{kartId}
     */
    @GetMapping("/users/by-card/{kartId}")
    public ResponseEntity<Personel> getUserByCardId(@PathVariable String kartId) {
        try {
            Optional<Personel> user = personelRepository.findByKartId(kartId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * TC Kimlik'e göre kullanıcı getir
     * GET /api/v1/users/by-tc/{tcKimlik}
     */
    @GetMapping("/users/by-tc/{tcKimlik}")
    public ResponseEntity<Personel> getUserByTcKimlik(@PathVariable String tcKimlik) {
        try {
            Optional<Personel> user = personelRepository.findByTcKimlik(tcKimlik);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Şubeye göre kullanıcıları getir
     * GET /api/v1/users/by-branch/{branchId}
     */
    @GetMapping("/users/by-branch/{branchId}")
    public ResponseEntity<List<Personel>> getUsersByBranch(@PathVariable Integer branchId) {
        try {
            List<Personel> users = personelRepository.findByBranchId(branchId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Aktif kullanıcıları getir
     * GET /api/v1/users/active
     */
    @GetMapping("/users/active")
    public ResponseEntity<List<Personel>> getActiveUsers() {
        try {
            List<Personel> activeUsers = personelRepository.findByAktifTrue();
            return ResponseEntity.ok(activeUsers);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    // ===== ACCESS DATA API ENDPOINTS =====
    
    /**
     * Tüm geçiş kayıtlarını getir
     * GET /api/v1/accessdata
     */
    @GetMapping("/accessdata")
    public ResponseEntity<List<GecisKaydi>> getAllAccessData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<GecisKaydi> accessDataPage = gecisKaydiRepository.findAll(pageable);
            
            return ResponseEntity.ok(accessDataPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * ID'ye göre geçiş kaydı getir
     * GET /api/v1/accessdata/{id}
     */
    @GetMapping("/accessdata/{id}")
    public ResponseEntity<GecisKaydi> getAccessDataById(@PathVariable Long id) {
        try {
            Optional<GecisKaydi> accessData = gecisKaydiRepository.findById(id);
            if (accessData.isPresent()) {
                return ResponseEntity.ok(accessData.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Kart ID'ye göre geçiş kayıtlarını getir
     * GET /api/v1/accessdata/by-card/{kartId}
     */
    @GetMapping("/accessdata/by-card/{kartId}")
    public ResponseEntity<List<GecisKaydi>> getAccessDataByCardId(@PathVariable String kartId) {
        try {
            List<GecisKaydi> accessData = gecisKaydiRepository.findByKartIdOrderByTarihDesc(kartId);
            return ResponseEntity.ok(accessData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Kullanıcı kayıt numarasına göre geçiş kayıtlarını getir
     * GET /api/v1/accessdata/by-user/{userKayitNo}
     */
    @GetMapping("/accessdata/by-user/{userKayitNo}")
    public ResponseEntity<List<GecisKaydi>> getAccessDataByUser(@PathVariable Long userKayitNo) {
        try {
            List<GecisKaydi> accessData = gecisKaydiRepository.findByUserKayitNoOrderByTarihDesc(userKayitNo);
            return ResponseEntity.ok(accessData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Şubeye göre geçiş kayıtlarını getir
     * GET /api/v1/accessdata/by-branch/{branchId}
     */
    @GetMapping("/accessdata/by-branch/{branchId}")
    public ResponseEntity<List<GecisKaydi>> getAccessDataByBranch(@PathVariable Integer branchId) {
        try {
            List<GecisKaydi> accessData = gecisKaydiRepository.findByBranchIdOrderByTarihDesc(branchId);
            return ResponseEntity.ok(accessData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Tarih aralığına göre geçiş kayıtlarını getir
     * GET /api/v1/accessdata/by-date-range?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
     */
    @GetMapping("/accessdata/by-date-range")
    public ResponseEntity<List<GecisKaydi>> getAccessDataByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<GecisKaydi> accessData = gecisKaydiRepository.findByTarihBetweenOrderByTarihDesc(start, end);
            return ResponseEntity.ok(accessData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Geçiş tipine göre geçiş kayıtlarını getir
     * GET /api/v1/accessdata/by-type/{gecisTipi}?startDate=2025-01-01T00:00:00&endDate=2025-01-31T23:59:59
     */
    @GetMapping("/accessdata/by-type/{gecisTipi}")
    public ResponseEntity<List<GecisKaydi>> getAccessDataByType(
            @PathVariable String gecisTipi,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<GecisKaydi> accessData = gecisKaydiRepository.findByGecisTipiAndTarihBetweenOrderByTarihDesc(
                gecisTipi, start, end);
            return ResponseEntity.ok(accessData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Belirli tarihten sonraki geçiş kayıtlarını getir
     * GET /api/v1/accessdata/after-date?date=2025-01-01T00:00:00
     */
    @GetMapping("/accessdata/after-date")
    public ResponseEntity<List<GecisKaydi>> getAccessDataAfterDate(@RequestParam String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
            
            List<GecisKaydi> accessData = gecisKaydiRepository.findByTarihAfter(dateTime);
            return ResponseEntity.ok(accessData);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

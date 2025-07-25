package com.gecisystems.merkezi.repository;

import com.gecisystems.merkezi.entity.GecisKaydi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GecisKaydiRepository extends JpaRepository<GecisKaydi, Long> {
    
    boolean existsByAccessId(Long accessId);
    boolean existsByKayitNo(Long kayitNo);
    boolean existsByKayitNoAndBranchId(Long kayitNo, Integer branchId);
    List<GecisKaydi> findByKartIdOrderByTarihDesc(String kartId);
    List<GecisKaydi> findByUserKayitNoOrderByTarihDesc(Long userKayitNo);
    List<GecisKaydi> findByAccessIdOrderByTarihDesc(Long accessId);
    List<GecisKaydi> findByTarihBetweenOrderByTarihDesc(LocalDateTime startDate, LocalDateTime endDate);
    List<GecisKaydi> findByKartIdAndTarihBetweenOrderByTarihDesc(String kartId, LocalDateTime startDate, LocalDateTime endDate);
    List<GecisKaydi> findByGecisTipiAndTarihBetweenOrderByTarihDesc(String gecisTipi, LocalDateTime startDate, LocalDateTime endDate);
    List<GecisKaydi> findByBranchIdOrderByTarihDesc(Integer branchId);
    
    List<GecisKaydi> findByTarihAfter(LocalDateTime tarih);
    
    @Query("SELECT g FROM GecisKaydi g WHERE " +
           "(g.kartId LIKE %:searchTerm% OR " +
           "g.kullaniciAdi LIKE %:searchTerm% OR " +
           "g.plaka LIKE %:searchTerm%) " +
           "ORDER BY g.tarih DESC")
    List<GecisKaydi> searchGecisKaydi(@Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(g) FROM GecisKaydi g")
    long countAllGecis();

    @Query("SELECT COUNT(g) FROM GecisKaydi g WHERE g.branchId = :branchId")
    long countByBranchId(@Param("branchId") Integer branchId);

    @Query("SELECT COUNT(g) FROM GecisKaydi g WHERE g.tarih >= :date")
    long countSince(@Param("date") LocalDateTime date);
}

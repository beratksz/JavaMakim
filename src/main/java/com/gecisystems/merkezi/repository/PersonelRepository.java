package com.gecisystems.merkezi.repository;

import com.gecisystems.merkezi.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonelRepository extends JpaRepository<Personel, Long> {
    
    // Access column isimlerine göre - BİREBİR ACCESS EŞLEŞMESİ
    Optional<Personel> findByTcKimlik(String tcKimlik);
    Optional<Personel> findByKartId(String kartId);
    Optional<Personel> findByAccessId(Long accessId);
    Optional<Personel> findByKayitNo(Long kayitNo);
    
    boolean existsByTcKimlik(String tcKimlik);
    boolean existsByKartId(String kartId);
    boolean existsByAccessId(Long accessId);
    boolean existsByKayitNo(Long kayitNo);
    
    List<Personel> findByAktifTrue();
    List<Personel> findByIptalFalse(); // Access: "Iptal" = false olan kayıtlar
    List<Personel> findBySubeNo(Integer subeNo); // Şube numarasına göre
    List<Personel> findByBranchId(Integer branchId);

    @Query("SELECT p FROM Personel p WHERE " +
           "(LOWER(p.adi) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.soyadi) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.tcKimlik LIKE %:searchTerm% OR " +
           "p.kartId LIKE %:searchTerm%) AND " +
           "p.aktif = true")
    List<Personel> searchPersonel(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(p) FROM Personel p WHERE p.aktif = true")
    long countActivePersonel();
    
    @Query("SELECT COUNT(p) FROM Personel p WHERE p.iptal = false OR p.iptal IS NULL")
    long countNonCancelledPersonel();
}

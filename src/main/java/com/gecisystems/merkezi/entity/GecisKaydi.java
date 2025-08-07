package com.gecisystems.merkezi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AccessDatas") // Access'teki tablo ismi AccessDatas
public class GecisKaydi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postgres_id") // PostgreSQL'e özel ID
    private Long id;
    
    // Access'teki gerçek column isimleri - TAM LİSTE
    @Column(name = "Kayit No")
    private Long kayitNo;
    
    @Column(name = "ID") // Access'teki ID
    private Long accessId;
    
    @Column(name = "Kart ID", length = 50)
    private String kartId;
    
    @Column(name = "Tarih")
    private LocalDateTime tarih;
    
    @Column(name = "Lokal Bolge No")
    private Integer lokalBolgeNo;
    
    @Column(name = "Global Bolge No")
    private Integer globalBolgeNo;
    
    @Column(name = "Panel ID")
    private Integer panelId;
    
    @Column(name = "Kapi ID")
    private Integer kapiId;
    
    @Column(name = "Gecis Tipi")
    private String gecisTipi;
    
    @Column(name = "Kod")
    private String kod;
    
    @Column(name = "Kullanici Tipi")
    private Integer kullaniciTipi;
    
    @Column(name = "Visitor Kayit No")
    private Long visitorKayitNo;
    
    @Column(name = "User Kayit No")
    private Long userKayitNo;
    
    @Column(name = "Kontrol")
    private String kontrol;
    
    @Column(name = "Kontrol Tarihi")
    private LocalDateTime kontrolTarihi;
    
    @Column(name = "Canli Resim")
    private byte[] canliResim;
    
    @Column(name = "Plaka", length = 20)
    private String plaka;
    
    @Column(name = "Kullanici Adi", length = 200)
    private String kullaniciAdi;
    
    @Column(name = "Islem Verisi 1")
    private String islemVerisi1;
    
    @Column(name = "Islem Verisi 2")
    private String islemVerisi2;
    
    // Ek alanlar
    @Column(name = "branch_id")
    private Integer branchId;
    
    // Geçiş anındaki personel bilgileri (ziyaretçi değişimi için)
    @Column(name = "gecis_anindaki_adi", length = 100)
    private String gecisAnindakiAdi;
    
    @Column(name = "gecis_anindaki_soyadi", length = 100)
    private String gecisAnindakiSoyadi;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public GecisKaydi() {}
    
    public GecisKaydi(String kartId, LocalDateTime tarih, String gecisTipi) {
        this.kartId = kartId;
        this.tarih = tarih;
        this.gecisTipi = gecisTipi;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getKayitNo() {
        return kayitNo;
    }
    
    public void setKayitNo(Long kayitNo) {
        this.kayitNo = kayitNo;
    }
    
    public Long getAccessId() {
        return accessId;
    }
    
    public void setAccessId(Long accessId) {
        this.accessId = accessId;
    }
    
    public String getKartId() {
        return kartId;
    }
    
    public void setKartId(String kartId) {
        this.kartId = kartId;
    }
    
    public LocalDateTime getTarih() {
        return tarih;
    }
    
    public void setTarih(LocalDateTime tarih) {
        this.tarih = tarih;
    }
    
    public Integer getLokalBolgeNo() {
        return lokalBolgeNo;
    }
    
    public void setLokalBolgeNo(Integer lokalBolgeNo) {
        this.lokalBolgeNo = lokalBolgeNo;
    }
    
    public Integer getGlobalBolgeNo() {
        return globalBolgeNo;
    }
    
    public void setGlobalBolgeNo(Integer globalBolgeNo) {
        this.globalBolgeNo = globalBolgeNo;
    }
    
    public Integer getPanelId() {
        return panelId;
    }
    
    public void setPanelId(Integer panelId) {
        this.panelId = panelId;
    }
    
    public Integer getKapiId() {
        return kapiId;
    }
    
    public void setKapiId(Integer kapiId) {
        this.kapiId = kapiId;
    }
    
    public String getGecisTipi() {
        return gecisTipi;
    }
    
    public void setGecisTipi(String gecisTipi) {
        this.gecisTipi = gecisTipi;
    }
    
    public String getKod() {
        return kod;
    }
    
    public void setKod(String kod) {
        this.kod = kod;
    }
    
    public Integer getKullaniciTipi() {
        return kullaniciTipi;
    }
    
    public void setKullaniciTipi(Integer kullaniciTipi) {
        this.kullaniciTipi = kullaniciTipi;
    }
    
    public Long getVisitorKayitNo() {
        return visitorKayitNo;
    }
    
    public void setVisitorKayitNo(Long visitorKayitNo) {
        this.visitorKayitNo = visitorKayitNo;
    }
    
    public Long getUserKayitNo() {
        return userKayitNo;
    }
    
    public void setUserKayitNo(Long userKayitNo) {
        this.userKayitNo = userKayitNo;
    }
    
    public String getKontrol() {
        return kontrol;
    }
    
    public void setKontrol(String kontrol) {
        this.kontrol = kontrol;
    }
    
    public LocalDateTime getKontrolTarihi() {
        return kontrolTarihi;
    }
    
    public void setKontrolTarihi(LocalDateTime kontrolTarihi) {
        this.kontrolTarihi = kontrolTarihi;
    }
    
    public byte[] getCanliResim() {
        return canliResim;
    }
    
    public void setCanliResim(byte[] canliResim) {
        this.canliResim = canliResim;
    }
    
    public String getPlaka() {
        return plaka;
    }
    
    public void setPlaka(String plaka) {
        this.plaka = plaka;
    }
    
    public String getKullaniciAdi() {
        return kullaniciAdi;
    }
    
    public void setKullaniciAdi(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
    }
    
    public String getIslemVerisi1() {
        return islemVerisi1;
    }
    
    public void setIslemVerisi1(String islemVerisi1) {
        this.islemVerisi1 = islemVerisi1;
    }
    
    public String getIslemVerisi2() {
        return islemVerisi2;
    }
    
    public void setIslemVerisi2(String islemVerisi2) {
        this.islemVerisi2 = islemVerisi2;
    }
    
    public Integer getBranchId() {
        return branchId;
    }
    
    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
    
    public String getGecisAnindakiAdi() {
        return gecisAnindakiAdi;
    }
    
    public void setGecisAnindakiAdi(String gecisAnindakiAdi) {
        this.gecisAnindakiAdi = gecisAnindakiAdi;
    }
    
    public String getGecisAnindakiSoyadi() {
        return gecisAnindakiSoyadi;
    }
    
    public void setGecisAnindakiSoyadi(String gecisAnindakiSoyadi) {
        this.gecisAnindakiSoyadi = gecisAnindakiSoyadi;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "GecisKaydi{" +
                "id=" + id +
                ", accessId=" + accessId +
                ", kartId='" + kartId + '\'' +
                ", tarih=" + tarih +
                ", gecisTipi='" + gecisTipi + '\'' +
                ", kullaniciAdi='" + kullaniciAdi + '\'' +
                ", gecisAnindakiAdi='" + gecisAnindakiAdi + '\'' +
                ", gecisAnindakiSoyadi='" + gecisAnindakiSoyadi + '\'' +
                ", branchId=" + branchId +
                '}';
    }
}

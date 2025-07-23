package com.gecisystems.merkezi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users") // Access'teki tablo ismi Users
public class Personel {
    
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
    
    @Column(name = "Dogrulama PIN", length = 10)
    private String dogrulamaPin;
    
    @Column(name = "Kimlik PIN", length = 10)
    private String kimlikPin;
    
    @Column(name = "Adi", length = 100)
    private String adi;
    
    @Column(name = "Soyadi", length = 100)
    private String soyadi;
    
    @Column(name = "Kullanici Tipi")
    private Integer kullaniciTipi;
    
    @Column(name = "Sifre", length = 50)
    private String sifre;
    
    @Column(name = "Gecis Modu")
    private Integer gecisModu;
    
    @Column(name = "Grup No")
    private Integer grupNo;
    
    @Column(name = "Visitor Grup No")
    private Integer visitorGrupNo;
    
    @Column(name = "Resim")
    private byte[] resim;
    
    @Column(name = "Plaka", length = 20)
    private String plaka;
    
    @Column(name = "TCKimlik", length = 11)
    private String tcKimlik;
    
    @Column(name = "Blok No")
    private Integer blokNo;
    
    @Column(name = "Daire", length = 10)
    private String daire;
    
    @Column(name = "Adres", length = 200)
    private String adres;
    
    @Column(name = "Gorev", length = 100)
    private String gorev;
    
    @Column(name = "Departman No")
    private Integer departmanNo;
    
    @Column(name = "Sirket No")
    private Integer sirketNo;
    
    @Column(name = "Aciklama", length = 200)
    private String aciklama;
    
    @Column(name = "Iptal")
    private Boolean iptal;
    
    @Column(name = "Grup Takvimi Aktif")
    private Boolean grupTakvimiAktif;
    
    @Column(name = "Grup Takvimi No")
    private Integer grupTakvimiNo;
    
    @Column(name = "Saat 1")
    private Integer saat1;
    
    @Column(name = "Grup No 1")
    private Integer grupNo1;
    
    @Column(name = "Saat 2")
    private Integer saat2;
    
    @Column(name = "Grup No 2")
    private Integer grupNo2;
    
    @Column(name = "Saat 3")
    private Integer saat3;
    
    @Column(name = "Grup No 3")
    private Integer grupNo3;
    
    @Column(name = "Tmp")
    private String tmp;
    
    @Column(name = "Sureli Kullanici")
    private Boolean sureliKullanici;
    
    @Column(name = "Bitis Tarihi")
    private LocalDateTime bitisTarihi;
    
    @Column(name = "Telefon", length = 20)
    private String telefon;
    
    @Column(name = "3 Grup")
    private Integer ucGrup;
    
    @Column(name = "Bitis Saati")
    private LocalDateTime bitisSaati;
    
    // Ek alanlar
    @Column(name = "aktif")
    private Boolean aktif = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Şube numarası (1 veya 2)
    @Column(name = "sube_no")
    private Integer subeNo;
    
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
    public Personel() {}
    
    public Personel(String tcKimlik, String adi, String soyadi) {
        this.tcKimlik = tcKimlik;
        this.adi = adi;
        this.soyadi = soyadi;
        this.aktif = true;
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
    
    public String getDogrulamaPin() {
        return dogrulamaPin;
    }
    
    public void setDogrulamaPin(String dogrulamaPin) {
        this.dogrulamaPin = dogrulamaPin;
    }
    
    public String getKimlikPin() {
        return kimlikPin;
    }
    
    public void setKimlikPin(String kimlikPin) {
        this.kimlikPin = kimlikPin;
    }
    
    public String getAdi() {
        return adi;
    }
    
    public void setAdi(String adi) {
        this.adi = adi;
    }
    
    public String getSoyadi() {
        return soyadi;
    }
    
    public void setSoyadi(String soyadi) {
        this.soyadi = soyadi;
    }
    
    public Integer getKullaniciTipi() {
        return kullaniciTipi;
    }
    
    public void setKullaniciTipi(Integer kullaniciTipi) {
        this.kullaniciTipi = kullaniciTipi;
    }
    
    public String getSifre() {
        return sifre;
    }
    
    public void setSifre(String sifre) {
        this.sifre = sifre;
    }
    
    public Integer getGecisModu() {
        return gecisModu;
    }
    
    public void setGecisModu(Integer gecisModu) {
        this.gecisModu = gecisModu;
    }
    
    public Integer getGrupNo() {
        return grupNo;
    }
    
    public void setGrupNo(Integer grupNo) {
        this.grupNo = grupNo;
    }
    
    public Integer getVisitorGrupNo() {
        return visitorGrupNo;
    }
    
    public void setVisitorGrupNo(Integer visitorGrupNo) {
        this.visitorGrupNo = visitorGrupNo;
    }
    
    public byte[] getResim() {
        return resim;
    }
    
    public void setResim(byte[] resim) {
        this.resim = resim;
    }
    
    public String getPlaka() {
        return plaka;
    }
    
    public void setPlaka(String plaka) {
        this.plaka = plaka;
    }
    
    public String getTcKimlik() {
        return tcKimlik;
    }
    
    public void setTcKimlik(String tcKimlik) {
        this.tcKimlik = tcKimlik;
    }
    
    public Integer getBlokNo() {
        return blokNo;
    }
    
    public void setBlokNo(Integer blokNo) {
        this.blokNo = blokNo;
    }
    
    public String getDaire() {
        return daire;
    }
    
    public void setDaire(String daire) {
        this.daire = daire;
    }
    
    public String getAdres() {
        return adres;
    }
    
    public void setAdres(String adres) {
        this.adres = adres;
    }
    
    public String getGorev() {
        return gorev;
    }
    
    public void setGorev(String gorev) {
        this.gorev = gorev;
    }
    
    public Integer getDepartmanNo() {
        return departmanNo;
    }
    
    public void setDepartmanNo(Integer departmanNo) {
        this.departmanNo = departmanNo;
    }
    
    public Integer getSirketNo() {
        return sirketNo;
    }
    
    public void setSirketNo(Integer sirketNo) {
        this.sirketNo = sirketNo;
    }
    
    public String getAciklama() {
        return aciklama;
    }
    
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
    
    public Boolean getIptal() {
        return iptal;
    }
    
    public void setIptal(Boolean iptal) {
        this.iptal = iptal;
    }
    
    public Boolean getGrupTakvimiAktif() {
        return grupTakvimiAktif;
    }
    
    public void setGrupTakvimiAktif(Boolean grupTakvimiAktif) {
        this.grupTakvimiAktif = grupTakvimiAktif;
    }
    
    public Integer getGrupTakvimiNo() {
        return grupTakvimiNo;
    }
    
    public void setGrupTakvimiNo(Integer grupTakvimiNo) {
        this.grupTakvimiNo = grupTakvimiNo;
    }
    
    public Integer getSaat1() {
        return saat1;
    }
    
    public void setSaat1(Integer saat1) {
        this.saat1 = saat1;
    }
    
    public Integer getGrupNo1() {
        return grupNo1;
    }
    
    public void setGrupNo1(Integer grupNo1) {
        this.grupNo1 = grupNo1;
    }
    
    public Integer getSaat2() {
        return saat2;
    }
    
    public void setSaat2(Integer saat2) {
        this.saat2 = saat2;
    }
    
    public Integer getGrupNo2() {
        return grupNo2;
    }
    
    public void setGrupNo2(Integer grupNo2) {
        this.grupNo2 = grupNo2;
    }
    
    public Integer getSaat3() {
        return saat3;
    }
    
    public void setSaat3(Integer saat3) {
        this.saat3 = saat3;
    }
    
    public Integer getGrupNo3() {
        return grupNo3;
    }
    
    public void setGrupNo3(Integer grupNo3) {
        this.grupNo3 = grupNo3;
    }
    
    public String getTmp() {
        return tmp;
    }
    
    public void setTmp(String tmp) {
        this.tmp = tmp;
    }
    
    public Boolean getSureliKullanici() {
        return sureliKullanici;
    }
    
    public void setSureliKullanici(Boolean sureliKullanici) {
        this.sureliKullanici = sureliKullanici;
    }
    
    public LocalDateTime getBitisTarihi() {
        return bitisTarihi;
    }
    
    public void setBitisTarihi(LocalDateTime bitisTarihi) {
        this.bitisTarihi = bitisTarihi;
    }
    
    public String getTelefon() {
        return telefon;
    }
    
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    
    public Integer getUcGrup() {
        return ucGrup;
    }
    
    public void setUcGrup(Integer ucGrup) {
        this.ucGrup = ucGrup;
    }
    
    public LocalDateTime getBitisSaati() {
        return bitisSaati;
    }
    
    public void setBitisSaati(LocalDateTime bitisSaati) {
        this.bitisSaati = bitisSaati;
    }
    
    public Boolean getAktif() {
        return aktif;
    }
    
    public void setAktif(Boolean aktif) {
        this.aktif = aktif;
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
    
    public Integer getSubeNo() {
        return subeNo;
    }
    
    public void setSubeNo(Integer subeNo) {
        this.subeNo = subeNo;
    }
    
    // Helper methods
    public String getFullName() {
        return adi + " " + soyadi;
    }
    
    @Override
    public String toString() {
        return "Personel{" +
                "id=" + id +
                ", accessId=" + accessId +
                ", tcKimlik='" + tcKimlik + '\'' +
                ", adi='" + adi + '\'' +
                ", soyadi='" + soyadi + '\'' +
                ", gorev='" + gorev + '\'' +
                ", aktif=" + aktif +
                '}';
    }
}

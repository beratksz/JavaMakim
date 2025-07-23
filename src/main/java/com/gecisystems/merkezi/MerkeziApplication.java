package com.gecisystems.merkezi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
public class MerkeziApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerkeziApplication.class, args);
        System.out.println("=================================");
        System.out.println("🚀 Merkezi Geçiş Sistemi Başlatıldı");
        System.out.println("🔄 Otomatik senkronizasyon aktif");
        System.out.println("⏰ Her 5 dakikada bir çalışacak");
        System.out.println("=================================");
    }
}
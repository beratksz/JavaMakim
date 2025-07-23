#!/bin/bash
# Quick Production Setup - quick-setup.sh

echo "🚀 JavaMakim Quick Production Setup"
echo "======================================"

# Sistem gereksinimlerini kontrol et
echo "🔍 Sistem kontrol ediliyor..."

# Java kontrol
if ! command -v java &> /dev/null; then
    echo "❌ Java bulunamadı! Java 11+ kurulu olmalı."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
echo "✅ Java versiyonu: $JAVA_VERSION"

# Maven kontrol (eğer build edilecekse)
if command -v mvn &> /dev/null; then
    echo "✅ Maven bulundu"
    
    echo "🔄 Proje build ediliyor..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✅ Build başarılı!"
    else
        echo "❌ Build başarısız!"
        exit 1
    fi
else
    echo "⚠️  Maven bulunamadı. JAR dosyası mevcut olmalı."
fi

# Konfigürasyon dosyasını kontrol et
if [ -f "src/main/resources/application-prod.properties" ]; then
    echo "✅ Production konfigürasyonu bulundu"
else
    echo "❌ application-prod.properties bulunamadı!"
    exit 1
fi

# JAR dosyasını kontrol et
if [ -f "target/JavaMakim-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ JAR dosyası hazır"
else
    echo "❌ JAR dosyası bulunamadı!"
    exit 1
fi

echo ""
echo "🎯 Production deployment hazır!"
echo ""
echo "Başlatmak için:"
echo "  Linux: ./start.sh"
echo "  Windows: start.bat"
echo ""
echo "Sistemd service için:"
echo "  sudo cp javamakım.service /etc/systemd/system/"
echo "  sudo systemctl enable javamakım"
echo "  sudo systemctl start javamakım"
echo ""
echo "Web interface: http://localhost:9090"

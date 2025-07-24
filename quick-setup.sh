#!/bin/bash
# Quick Production Setup - quick-setup.sh

echo "🚀 JavaMakim Quick Production Setup"
echo "======================================"

# Sistem gereksinimlerini kontrol et
echo "🔍 Sistem kontrol ediliyor..."

# ...existing code...

# JAR dosyasını otomatik bul ve çalıştır
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" -not -name "*-original.jar" | head -1)

if [ -n "$JAR_FILE" ]; then
    echo "✅ JAR dosyası bulundu: $(basename $JAR_FILE)"
    echo ""
    echo "🚀 Uygulama başlatılıyor..."
    echo "📝 Console çıktısını görmek için Ctrl+C ile durdurun"
    echo "� Web arayüz: http://localhost:9090"
    echo "======================================"
    echo ""
    
    # JAR dosyasını direkt çalıştır
    java -jar "$JAR_FILE"
else
    echo "❌ JAR dosyası bulunamadı!"
    echo "🔨 Önce projeyi build edin: mvn clean package -DskipTests"
    exit 1
fi
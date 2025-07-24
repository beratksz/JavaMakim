#!/bin/bash
# Production Build Script - build.sh

echo "🚀 Production JAR dosyası oluşturuluyor..."

# Maven ile clean ve package
mvn clean package -DskipTests

# JAR dosyasını kontrol et
if [ -f "target/JavaMakim-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ JAR dosyası başarıyla oluşturuldu: target/JavaMakim-0.0.1-SNAPSHOT.jar"
    echo "📦 Dosya boyutu: $(du -h target/JavaMakim-0.0.1-SNAPSHOT.jar | cut -f1)"
else
    echo "❌ JAR dosyası oluşturulamadı!"
    exit 1
fi

echo "🎯 Production deployment için hazır!"

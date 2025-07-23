#!/bin/bash
# Production Startup Script - start.sh

# Değişkenler
APP_NAME="JavaMakim"
JAR_FILE="JavaMakim-0.0.1-SNAPSHOT.jar"
PID_FILE="/var/run/javamakım.pid"
LOG_FILE="/var/log/javamakım/application.log"

# Java parametreleri
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
SPRING_OPTS="--spring.profiles.active=prod"

echo "🚀 $APP_NAME Production başlatılıyor..."

# Log dizinini oluştur
mkdir -p /var/log/javamakım

# Önceki process'i kontrol et
if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null 2>&1; then
        echo "⚠️  $APP_NAME zaten çalışıyor (PID: $PID)"
        exit 1
    else
        rm $PID_FILE
    fi
fi

# Uygulamayı başlat
echo "🔄 JAR dosyası çalıştırılıyor..."
nohup java $JAVA_OPTS -jar $JAR_FILE $SPRING_OPTS > $LOG_FILE 2>&1 &

# PID'i kaydet
echo $! > $PID_FILE

echo "✅ $APP_NAME başarıyla başlatıldı!"
echo "📝 PID: $(cat $PID_FILE)"
echo "📋 Log dosyası: $LOG_FILE"
echo "🌐 URL: http://localhost:9090"

# Başlatma durumunu kontrol et
sleep 5
if ps -p $(cat $PID_FILE) > /dev/null 2>&1; then
    echo "🎯 Servis başarıyla çalışıyor!"
else
    echo "❌ Servis başlatılamadı! Log dosyasını kontrol edin."
    rm $PID_FILE
    exit 1
fi

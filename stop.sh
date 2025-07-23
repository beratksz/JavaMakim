#!/bin/bash
# Production Stop Script - stop.sh

APP_NAME="JavaMakim"
PID_FILE="/var/run/javamakım.pid"

echo "🛑 $APP_NAME durdruluyor..."

if [ ! -f $PID_FILE ]; then
    echo "⚠️  PID dosyası bulunamadı. Uygulama çalışmıyor olabilir."
    exit 1
fi

PID=$(cat $PID_FILE)

if ps -p $PID > /dev/null 2>&1; then
    echo "🔄 Process durduruluyor (PID: $PID)..."
    kill $PID
    
    # Graceful shutdown bekle
    sleep 10
    
    # Hala çalışıyorsa force kill
    if ps -p $PID > /dev/null 2>&1; then
        echo "⚡ Force kill uygulanıyor..."
        kill -9 $PID
        sleep 2
    fi
    
    # PID dosyasını sil
    rm $PID_FILE
    echo "✅ $APP_NAME başarıyla durduruldu!"
else
    echo "⚠️  Process bulunamadı (PID: $PID)"
    rm $PID_FILE
fi

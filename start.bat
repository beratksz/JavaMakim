@echo off
REM Production Startup Script - start.bat (Windows)

set APP_NAME=JavaMakim
set JAR_FILE=JavaMakim-0.0.1-SNAPSHOT.jar
set LOG_FILE=application.log

echo 🚀 %APP_NAME% Production başlatılıyor...

REM Java parametreleri
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
set SPRING_OPTS=--spring.profiles.active=prod

REM Uygulamayı başlat
echo 🔄 JAR dosyası çalıştırılıyor...
start "JavaMakim" java %JAVA_OPTS% -jar %JAR_FILE% %SPRING_OPTS%

echo ✅ %APP_NAME% başlatıldı!
echo 🌐 URL: http://localhost:9090
echo 📋 Tarayıcıda kontrol edebilirsiniz.

pause

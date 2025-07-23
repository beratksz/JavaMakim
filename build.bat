# Production Build Script - build.bat (Windows)

@echo off
echo 🚀 Production JAR dosyası oluşturuluyor...

:: Maven ile clean ve package
call mvn clean package -DskipTests

:: JAR dosyasını kontrol et
if exist "target\JavaMakim-0.0.1-SNAPSHOT.jar" (
    echo ✅ JAR dosyası başarıyla oluşturuldu: target\JavaMakim-0.0.1-SNAPSHOT.jar
    for %%I in (target\JavaMakim-0.0.1-SNAPSHOT.jar) do echo 📦 Dosya boyutu: %%~zI bytes
) else (
    echo ❌ JAR dosyası oluşturulamadı!
    pause
    exit /b 1
)

echo 🎯 Production deployment için hazır!
pause

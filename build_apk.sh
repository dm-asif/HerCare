#!/usr/bin/env bash
set -e

echo "=== HerCare APK Build Script ==="

# 1. Restore debug.keystore
if [ -f debug.keystore.base64 ] && [ ! -f debug.keystore ]; then
  echo "Restoring debug.keystore from debug.keystore.base64..."
  base64 -d debug.keystore.base64 > debug.keystore
fi

# 2. Setup .env file
if [ ! -f .env ]; then
  echo "Creating .env from .env.example..."
  if [ -f .env.example ]; then
    cp .env.example .env
  else
    touch .env
  fi
fi

# 3. Make gradlew executable
chmod +x gradlew

# 4. Build APK
echo "Building Debug APK..."
./gradlew assembleDebug --stacktrace

echo "=== Build Completed Successfully! ==="
echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
ls -lh app/build/outputs/apk/debug/*.apk

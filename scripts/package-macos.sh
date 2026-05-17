#!/bin/bash
# ─── Rebuild the macOS .app from the latest JAR ───────────────────────────────
# Run from the project root after  mvn package
# Output: dist/macos/Segue o Ritmo.app  (requires Java 17 on host machine)

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
APP="$PROJECT_DIR/dist/macos/Segue o Ritmo.app"
JAR="$PROJECT_DIR/target/simon-says.jar"

cd "$PROJECT_DIR"

# Build fat JAR
mvn package -q

# Rebuild .app skeleton
rm -rf "$APP"
mkdir -p "$APP/Contents/MacOS"
mkdir -p "$APP/Contents/app"

# Info.plist
cp "$PROJECT_DIR/dist/macos/Segue o Ritmo.app/../../../dist/macos/Segue o Ritmo.app/Contents/Info.plist" \
   "$APP/Contents/Info.plist" 2>/dev/null || \
cat > "$APP/Contents/Info.plist" << 'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
    "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>    <string>SegueoRitmo</string>
    <key>CFBundleIdentifier</key>   <string>com.blip.segueoRitmo</string>
    <key>CFBundleName</key>         <string>Segue o Ritmo</string>
    <key>CFBundleDisplayName</key>  <string>Segue o Ritmo</string>
    <key>CFBundlePackageType</key>  <string>APPL</string>
    <key>CFBundleVersion</key>      <string>1.0.0</string>
    <key>CFBundleShortVersionString</key> <string>1.0.0</string>
    <key>LSMinimumSystemVersion</key> <string>10.14</string>
    <key>NSHighResolutionCapable</key> <true/>
</dict>
</plist>
PLIST

# Launcher script
cat > "$APP/Contents/MacOS/SegueoRitmo" << 'LAUNCHER'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
exec java -Xmx512m -Dfile.encoding=UTF-8 \
          -Dapple.awt.application.name="Segue o Ritmo" \
          -jar "$SCRIPT_DIR/../app/simon-says.jar"
LAUNCHER

chmod +x "$APP/Contents/MacOS/SegueoRitmo"
cp "$JAR" "$APP/Contents/app/simon-says.jar"

echo "✓  .app rebuilt: $APP"
echo "   Copia 'Segue o Ritmo.app' para /Applications ou para a Área de Trabalho."

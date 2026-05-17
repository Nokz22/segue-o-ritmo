# 🎵 Segue o Ritmo

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven)](https://maven.apache.org/)
[![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Windows-0078D4)](https://github.com)
[![Tests](https://img.shields.io/badge/JUnit%205-11%20tests-25A162?logo=junit5)](https://junit.org/junit5/)

> A Simon Says memory game built in Java 17 + Swing, designed for elderly users and people with cognitive difficulties. Touch-screen-ready, full-screen kiosk mode, European Portuguese UI.

---

## 🎮 Gameplay

Coloured buttons light up one by one — repeat the sequence in the same order. Each successful round adds another colour. How far can you go?

Two game modes are available from the main menu:

| Mode | Description |
|------|-------------|
| **Classic** | The sequence grows by one colour each round (classic Simon Says) |
| **Random** | A brand-new random sequence is generated every round — more challenging |

Three difficulty levels with dramatically different speeds make the game accessible at any pace:

| Level | Interval | For whom |
|-------|----------|----------|
| 🐢 **Slow** | 2 500 ms | Users who need extra time |
| 🚶 **Normal** | 950 ms | Standard pace |
| 🐇 **Fast** | 450 ms | Fast-reflex challenge |

> The in-game UI is displayed in European Portuguese (pt-PT) as the game targets Portuguese-speaking users.

---

## ✨ Features

- **Full-screen kiosk mode** — launches full-screen by default, toggle with `F11` or the corner button
- **Touch-screen support** — all interactions use `mousePressed` for reliable tap detection on touch displays
- **Two game modes** — Classic (accumulating) and Random (fresh sequence each round)
- **High-contrast UI** — radial-gradient flash animations, bright vs near-black button states, white border hover
- **Programmatic audio** — pure sine-wave tones generated at runtime with `javax.sound.sampled`, no external audio files
- **Round tracker & personal best** — star display updates after every successful round
- **Accessible design** — traffic-light colour coding, difficulty dots (●●●), large touch targets, emoji + text rendered separately for reliable font metrics
- **European Portuguese UI** — all player-visible strings centralised in `GameConfig`

---

## 🚀 Quick Start

### Requirements

- **Java 17+** — download from [adoptium.net](https://adoptium.net/) if not already installed

### Run on any platform

```bash
java -jar dist/windows/simon-says.jar
```

### macOS

Double-click **`dist/macos/Segue o Ritmo.app`**

> First run: macOS may show a "developer cannot be verified" warning — right-click the app → **Open** → **Open** to bypass Gatekeeper once.

### Windows

Double-click **`dist/windows/Segue o Ritmo.bat`**

---

## 🔨 Build from Source

```bash
# 1. Clone
git clone https://github.com/Nokz22/segue-o-ritmo.git
cd segue-o-ritmo

# 2. Build fat JAR (runs tests automatically)
mvn package

# 3. Launch
java -jar target/simon-says.jar
```

Run tests only:

```bash
mvn test
```

---

## 🏗️ Architecture

Strict **MVC** with four supporting design patterns:

```
com.followtheritm
├── Main.java                  Bootstrap & wiring
├── config/   GameConfig       Singleton — all constants & pt-PT UI strings
├── model/    GameSession       Mutable state machine (IDLE → SHOWING → AWAITING_INPUT → …)
│             GameSequence      Immutable record (List.copyOf)
│             PlayerInput       Immutable record with withAdded()
│             SimonColor        Enum — colour + frequency + dark/bright variants
│             GameMode          Enum — CLASSIC | RANDOM
├── observer/ GameEvent         Enum — events fired by GameSession
│             GameEventListener @FunctionalInterface
├── service/  SequenceService   Interface: extend / createInitial / createRandom
│             AudioServiceImpl  Singleton — sine-wave synthesis, daemon thread
├── strategy/ SpeedStrategy     Interface: intervalMs / flashDurationMs
│             SlowSpeed / NormalSpeed / FastSpeed
├── factory/  ColorButtonFactory
└── view/     MainFrame         Root JFrame — CardLayout (menu ↔ game)
              MainMenuPanel     Landing screen — mode selection
              SimonPanel        JLayeredPane — 2×2 button grid + overlay
              ControlPanel      Bottom bar — start, speed, menu, full-screen
              ColorButton       Custom-painted button with glow animation
              GameOverOverlay   Success / error card
              RoundIndicator    Pill + star row + personal best
```

**Key design decisions:**

- All Swing mutations use `javax.swing.Timer` — never `Thread.sleep` on the EDT
- `JLayeredPane.doLayout()` override replaces `ComponentListener` to avoid zero-size children on first paint
- `GameOverOverlay.dismiss()` avoids infinite recursion with the deprecated `Component.hide()`
- Emoji and plain text are rendered separately because `FontMetrics.stringWidth()` is unreliable for emoji glyphs

---

## ♿ Accessibility Design Rationale

This game was designed for use on **workplace touch-screen computers** by users with cognitive difficulties:

- **Large buttons** — 2×2 grid fills the full square game area
- **Dramatic state contrast** — active buttons use a bright radial-gradient flash; idle buttons are near-black; hover adds a 5 px white border
- **Speed gaps are intentional** — Slow (2 500 ms) vs Normal (950 ms) vs Fast (450 ms) ensures users perceive a clearly different pace rather than a subtle change
- **No reading required** — mode cards use colour + icon; difficulty buttons use colour + dots (●); feedback overlay uses large ✓ / ✗ symbols
- **Touch-first input** — `mousePressed` fires immediately on finger-down, unlike `mouseClicked` which requires a full tap-and-release cycle

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 (records, switch expressions) |
| UI | Swing — `JLayeredPane`, `CardLayout`, `GraphicsDevice` full-screen |
| Audio | `javax.sound.sampled` — programmatic sine-wave generation |
| Build | Apache Maven 3 + `maven-assembly-plugin` (fat JAR) |
| Testing | JUnit 5 — 11 unit tests covering model and service layers |

---

## 📁 Project Structure

```
segue-o-ritmo/
├── pom.xml
├── src/
│   ├── main/java/com/followtheritm/   # Game source (25 classes)
│   └── test/java/com/followtheritm/   # Unit tests (2 test classes, 11 tests)
├── dist/
│   ├── macos/
│   │   └── Segue o Ritmo.app/         # macOS launcher (requires Java 17)
│   └── windows/
│       ├── simon-says.jar             # Pre-built fat JAR — run on any platform
│       └── Segue o Ritmo.bat          # Windows double-click launcher
└── scripts/
    ├── package-macos.sh               # Rebuild the .app after mvn package
    └── package-windows.bat            # Build Windows .exe with jpackage
```

---

## 📝 Language Convention

| Context | Language |
|---------|----------|
| All source code (classes, methods, variables, comments) | **English** |
| All player-visible text (UI strings in `GameConfig`) | **European Portuguese (pt-PT)** |

---

*Built with Java 17 · Swing · No external dependencies*

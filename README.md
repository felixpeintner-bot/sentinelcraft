# Sentinel Craft (Fabric, Minecraft 26.1.x)

Sicherheitstechnik-Mod: Keycard-Leser, Überwachungskameras, ein Generator und ein **unsichtbares Kabelnetz**,
mit dem man Lampen, Schalter, Leser und Kameras verbindet – zusätzlich zu normalem Redstone.

> **Status:** Der Code wurde geschrieben, aber noch nicht kompiliert oder im Spiel getestet.
> Beim ersten Bauen können kleine Fehler auftauchen – Fehlermeldung kopieren, dann wird sie korrigiert.

## Jar bauen

### Variante A: lokal
1. **JDK 25** installieren (z. B. Temurin 25) und sicherstellen, dass `java -version` 25 zeigt.
2. Im Projektordner:
   - Windows: `gradlew.bat build`
   - Linux/macOS: `./gradlew build`
3. Die Jar liegt danach in `build/libs/sentinelcraft-0.1.0.jar`
   (**nicht** die Datei mit `-sources` im Namen).

### Variante B: ohne Installation, über GitHub
1. Neues Repository auf github.com anlegen und diesen Ordner hochladen.
2. Reiter **Actions** → „Jar bauen" läuft automatisch (oder „Run workflow").
3. Nach ca. 3–5 Minuten unter dem Lauf bei **Artifacts** die `sentinelcraft-jar` herunterladen.

## Installieren
- Minecraft 26.1.x mit **Fabric Loader** (≥ 0.18) und **Fabric API** installieren.
- `sentinelcraft-0.1.0.jar` in den `mods`-Ordner legen.

## Bedienung

| Gerät | Rezept (Kurzform) | Verhalten |
|---|---|---|
| **Generator** | Eisen, Ofen, Redstone, Kupfer | Mit Kohle/Holzkohle/Kohleblock/Lohenrute füttern → liefert Strom. Rechtsklick leer zeigt die Restzeit. |
| **Kabel-Werkzeug** | Kupfer, Redstone, Eisen, Stock | Gerät A anklicken, dann Gerät B → Kabel verbunden. Dasselbe Paar nochmal → Kabel entfernt. Mit dem Werkzeug in der Hand sieht man die Kabel als Partikel-Linien. Shift + Rechtsklick in die Luft bricht die Auswahl ab. Max. 24 Blöcke Abstand, max. 8 Kabel pro Gerät. |
| **Kabellampe** | Redstone-Lampe + Kupfer | Leuchtet bei Strom aus dem Kabelnetz **oder** Redstone-Signal. |
| **Lichtschalter** | Hebel + Kupfer | Rechtsklick schaltet den Strom im Kabelnetz durch/aus und gibt Redstone-Signal. |
| **Keycard-Leser** | Eisen, Knopf, Redstone | Keycard in die Hand + Rechtsklick: 3 s Redstone-Signal, leitet im Kabelnetz Strom durch. Shift + Rechtsklick (Besitzer): benötigte Stufe 1/2/3 wechseln. |
| **Keycard 1/2/3** | Papier + Eisen + Redstone → Stufe 2 mit Gold → Stufe 3 mit Diamant | Höhere Karten öffnen auch Leser niedrigerer Stufe. |
| **Überwachungskamera** | Eisen, Glasscheibe, Spinnenauge, Redstone | Erkennt Spieler im Sichtfeld (14 Blöcke, freie Sicht nötig): Redstone-Signal, leitet im Kabelnetz Strom durch, meldet dem Besitzer im Chat. Shift + Rechtsklick (Besitzer): Besitzer ignorieren an/aus (zum Testen allein auf „erkennt jeden" stellen). |

### Beispiel: Lampe mit Schalter
1. Generator, Lichtschalter und Kabellampe aufstellen.
2. Generator mit Kohle füttern.
3. Kabel-Werkzeug: Generator → Schalter, dann Schalter → Lampe.
4. Schalter anklicken → Lampe geht an.

### Beispiel: Alarmlicht
Generator → Kamera → Lampe verbinden: Die Lampe leuchtet, solange die Kamera jemanden sieht.

## Projekt anpassen
- Texturen, Modelle, Rezepte, Sprachtexte: `tools/gen_assets.py` bearbeiten und `python3 tools/gen_assets.py` ausführen (braucht `pip install pillow`).
- Java-Code: `src/main/java/com/sentinelcraft/`
- Die Versionen stehen in `gradle.properties`.

## Bekannte Grenzen (Version 0.1.0)
- Kameras erkennen und melden, haben aber noch keine Live-Bildübertragung auf einen Monitor.
- Keycard-Leser und Kameras sind noch nicht gegen Abbauen durch andere Spieler geschützt.
- Texturen sind einfache generierte Platzhalter.

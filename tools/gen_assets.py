#!/usr/bin/env python3
"""Erzeugt Texturen, Modelle, Blockstates, Rezepte, Loot-Tables und Sprachdateien fuer Sentinel Craft.

Aufruf (aus dem Projektordner):  python3 tools/gen_assets.py
Braucht: pip install pillow
Aendere hier Texturen/Rezepte/Texte und fuehre das Skript erneut aus.
"""
import json
import math
import random
from pathlib import Path

from PIL import Image

MOD = "sentinelcraft"
ROOT = Path(__file__).resolve().parent.parent / "src" / "main" / "resources"
ASSETS = ROOT / "assets" / MOD
DATA = ROOT / "data" / MOD


def write_json(path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


# ------------------------------------------------------------------ Texturen

def new(color=(0, 0, 0, 0)):
    return Image.new("RGBA", (16, 16), color)


def rect(im, x0, y0, x1, y1, c):
    if len(c) == 3:
        c = (*c, 255)
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            im.putpixel((x, y), c)


def outline(im, x0, y0, x1, y1, c):
    rect(im, x0, y0, x1, y0, c)
    rect(im, x0, y1, x1, y1, c)
    rect(im, x0, y0, x0, y1, c)
    rect(im, x1, y0, x1, y1, c)


def noisy(base, seed, amp=6):
    rnd = random.Random(seed)
    im = new()
    for y in range(16):
        for x in range(16):
            d = rnd.randint(-amp, amp)
            im.putpixel((x, y), tuple(max(0, min(255, v + d)) for v in base) + (255,))
    return im


def save(im, kind, name):
    p = ASSETS / "textures" / kind / f"{name}.png"
    p.parent.mkdir(parents=True, exist_ok=True)
    im.save(p)


def tex_metal_side():
    im = noisy((110, 110, 118), 1)
    outline(im, 0, 0, 15, 15, (70, 70, 78))
    return im


def tex_generator(on):
    im = noisy((72, 74, 82), 2)
    outline(im, 0, 0, 15, 15, (40, 40, 46))
    for y in (4, 6, 8, 10, 12):
        rect(im, 3, y, 12, y, (255, 165, 40) if on else (35, 35, 40))
    if on:
        rect(im, 6, 7, 9, 9, (255, 235, 140))
    rect(im, 13, 2, 14, 3, (255, 170, 40) if on else (90, 50, 20))
    return im


def tex_lamp(on):
    im = noisy((255, 226, 130) if on else (112, 96, 64), 3, 5)
    frame = (190, 150, 70) if on else (70, 60, 40)
    outline(im, 0, 0, 15, 15, frame)
    rect(im, 5, 1, 5, 14, frame)
    rect(im, 10, 1, 10, 14, frame)
    rect(im, 1, 5, 14, 5, frame)
    rect(im, 1, 10, 14, 10, frame)
    if on:
        rect(im, 6, 6, 9, 9, (255, 255, 235))
    return im


def tex_switch(on):
    im = noisy((190, 190, 196), 4, 4)
    outline(im, 0, 0, 15, 15, (80, 80, 88))
    rect(im, 4, 3, 11, 12, (60, 60, 68))
    if on:
        rect(im, 7, 4, 8, 8, (235, 235, 240))
        rect(im, 6, 3, 9, 4, (255, 255, 255))
    else:
        rect(im, 7, 7, 8, 11, (235, 235, 240))
        rect(im, 6, 11, 9, 12, (255, 255, 255))
    rect(im, 13, 2, 14, 3, (60, 230, 90) if on else (60, 70, 60))
    return im


def tex_reader(ok):
    im = noisy((44, 44, 54), 5, 3)
    outline(im, 0, 0, 15, 15, (20, 20, 26))
    rect(im, 3, 6, 12, 6, (95, 95, 105))
    rect(im, 3, 7, 12, 8, (8, 8, 10))
    rect(im, 12, 2, 13, 3, (60, 230, 90) if ok else (225, 45, 45))
    for x in (4, 7, 10):
        rect(im, x, 11, x + 1, 12, (120, 120, 130))
    return im


def tex_camera(alert):
    im = noisy((34, 34, 40), 6, 3)
    outline(im, 0, 0, 15, 15, (12, 12, 16))
    cx = cy = 7.5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if d <= 5.2:
                im.putpixel((x, y), (14, 14, 20, 255))
            if d <= 3.4:
                im.putpixel((x, y), (140, 30, 30, 255) if alert else (40, 64, 110, 255))
            if d <= 1.4:
                im.putpixel((x, y), (255, 90, 90, 255) if alert else (120, 170, 230, 255))
    rect(im, 13, 2, 14, 3, (255, 40, 40) if alert else (110, 20, 20))
    return im


def tex_keycard(level):
    base = {1: (60, 170, 90), 2: (60, 120, 210), 3: (205, 60, 60)}[level]
    dark = tuple(int(v * 0.6) for v in base)
    im = new()
    rect(im, 2, 4, 13, 11, base)
    outline(im, 2, 4, 13, 11, dark)
    rect(im, 3, 5, 12, 6, (30, 30, 34))
    rect(im, 4, 8, 6, 9, (235, 205, 90))
    rect(im, 8, 9, 12, 9, (245, 245, 245))
    for i in range(level):
        rect(im, 8 + i * 2, 8, 8 + i * 2, 8, (255, 255, 255))
    return im


def tex_cable_tool():
    im = new()
    brown, light = (120, 80, 45), (160, 110, 65)
    for i in range(7):
        im.putpixel((3 + i, 13 - i), (*brown, 255))
        im.putpixel((4 + i, 13 - i), (*light, 255))
    cx, cy = 11, 5
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - cx, y - cy)
            if 2.0 <= d <= 3.6:
                im.putpixel((x, y), (205, 115, 55, 255))
            elif d < 2.0:
                im.putpixel((x, y), (140, 70, 30, 255))
    return im


save(tex_metal_side(), "block", "metal_side")
save(tex_generator(False), "block", "generator_off")
save(tex_generator(True), "block", "generator_on")
save(tex_lamp(False), "block", "wire_lamp_off")
save(tex_lamp(True), "block", "wire_lamp_on")
save(tex_switch(False), "block", "light_switch_off")
save(tex_switch(True), "block", "light_switch_on")
save(tex_reader(False), "block", "keycard_reader_idle")
save(tex_reader(True), "block", "keycard_reader_ok")
save(tex_camera(False), "block", "camera_idle")
save(tex_camera(True), "block", "camera_alert")
for lv in (1, 2, 3):
    save(tex_keycard(lv), "item", f"keycard_{lv}")
save(tex_cable_tool(), "item", "cable_tool")

# ------------------------------------------------------------------ Modelle


def cube_model(name, tex):
    write_json(ASSETS / "models" / "block" / f"{name}.json",
               {"parent": "minecraft:block/cube_all", "textures": {"all": f"{MOD}:block/{tex}"}})


def box_model(name, front_tex, frm, to):
    """Quader, Vorderseite = Nordseite (Rotation uebernimmt der Blockstate)."""
    side = {"uv": [0, 0, 16, 16], "texture": "#side"}
    write_json(ASSETS / "models" / "block" / f"{name}.json", {
        "parent": "minecraft:block/block",
        "textures": {"front": f"{MOD}:block/{front_tex}", "side": f"{MOD}:block/metal_side",
                     "particle": f"{MOD}:block/metal_side"},
        "elements": [{
            "from": frm, "to": to,
            "faces": {
                "north": {"uv": [0, 0, 16, 16], "texture": "#front"},
                "south": side, "east": side, "west": side, "up": side, "down": side,
            },
        }],
    })


cube_model("generator_off", "generator_off")
cube_model("generator_on", "generator_on")
cube_model("wire_lamp_off", "wire_lamp_off")
cube_model("wire_lamp_on", "wire_lamp_on")
PANEL = ([3, 3, 13], [13, 13, 16])
CAM = ([5, 5, 8], [11, 11, 16])
box_model("light_switch_off", "light_switch_off", *PANEL)
box_model("light_switch_on", "light_switch_on", *PANEL)
box_model("keycard_reader_idle", "keycard_reader_idle", *PANEL)
box_model("keycard_reader_ok", "keycard_reader_ok", *PANEL)
box_model("camera_idle", "camera_idle", *CAM)
box_model("camera_alert", "camera_alert", *CAM)

# ------------------------------------------------------------------ Blockstates

ROT = {"north": 0, "east": 90, "south": 180, "west": 270}


def lit_state(name, off, on):
    write_json(ASSETS / "blockstates" / f"{name}.json", {"variants": {
        "lit=false": {"model": f"{MOD}:block/{off}"},
        "lit=true": {"model": f"{MOD}:block/{on}"},
    }})


def facing_state(name, off, on):
    variants = {}
    for facing, rot in ROT.items():
        for powered, model in (("false", off), ("true", on)):
            entry = {"model": f"{MOD}:block/{model}"}
            if rot:
                entry["y"] = rot
            variants[f"facing={facing},powered={powered}"] = entry
    write_json(ASSETS / "blockstates" / f"{name}.json", {"variants": variants})


lit_state("generator", "generator_off", "generator_on")
lit_state("wire_lamp", "wire_lamp_off", "wire_lamp_on")
facing_state("light_switch", "light_switch_off", "light_switch_on")
facing_state("keycard_reader", "keycard_reader_idle", "keycard_reader_ok")
facing_state("camera", "camera_idle", "camera_alert")

# ------------------------------------------------------------------ Item-Definitionen (1.21.4+)


def item_def(name, model):
    write_json(ASSETS / "items" / f"{name}.json", {"model": {"type": "minecraft:model", "model": f"{MOD}:{model}"}})


for name, model in (("generator", "block/generator_off"), ("wire_lamp", "block/wire_lamp_off"),
                    ("light_switch", "block/light_switch_off"), ("keycard_reader", "block/keycard_reader_idle"),
                    ("camera", "block/camera_idle")):
    item_def(name, model)

for name, parent in (("cable_tool", "minecraft:item/handheld"), ("keycard_1", "minecraft:item/generated"),
                     ("keycard_2", "minecraft:item/generated"), ("keycard_3", "minecraft:item/generated")):
    write_json(ASSETS / "models" / "item" / f"{name}.json",
               {"parent": parent, "textures": {"layer0": f"{MOD}:item/{name}"}})
    item_def(name, f"item/{name}")

# ------------------------------------------------------------------ Rezepte


def shaped(name, pattern, key, result, count=1, category="redstone"):
    write_json(DATA / "recipe" / f"{name}.json", {
        "type": "minecraft:crafting_shaped", "category": category,
        "pattern": pattern, "key": key, "result": {"id": f"{MOD}:{result}", "count": count},
    })


def shapeless(name, ingredients, result, count=1, category="redstone"):
    write_json(DATA / "recipe" / f"{name}.json", {
        "type": "minecraft:crafting_shapeless", "category": category,
        "ingredients": ingredients, "result": {"id": f"{MOD}:{result}", "count": count},
    })


shaped("generator", ["III", "IFI", "RCR"],
       {"I": "minecraft:iron_ingot", "F": "minecraft:furnace", "R": "minecraft:redstone", "C": "minecraft:copper_ingot"},
       "generator")
shaped("cable_tool", ["C R", " I ", " S "],
       {"C": "minecraft:copper_ingot", "R": "minecraft:redstone", "I": "minecraft:iron_ingot", "S": "minecraft:stick"},
       "cable_tool", category="equipment")
shapeless("light_switch", ["minecraft:lever", "minecraft:copper_ingot"], "light_switch")
shapeless("wire_lamp", ["minecraft:redstone_lamp", "minecraft:copper_ingot"], "wire_lamp")
shaped("keycard_reader", ["III", "IBR", "III"],
       {"I": "minecraft:iron_ingot", "B": "minecraft:stone_button", "R": "minecraft:redstone"}, "keycard_reader")
shaped("camera", ["III", "GEG", "IRI"],
       {"I": "minecraft:iron_ingot", "G": "minecraft:glass_pane", "E": "minecraft:spider_eye", "R": "minecraft:redstone"},
       "camera")
shapeless("keycard_1", ["minecraft:paper", "minecraft:iron_ingot", "minecraft:redstone"], "keycard_1", category="misc")
shapeless("keycard_2", [f"{MOD}:keycard_1", "minecraft:gold_ingot"], "keycard_2", category="misc")
shapeless("keycard_3", [f"{MOD}:keycard_2", "minecraft:diamond"], "keycard_3", category="misc")

# ------------------------------------------------------------------ Loot-Tables + Tags

BLOCKS = ["generator", "wire_lamp", "light_switch", "keycard_reader", "camera"]
for b in BLOCKS:
    write_json(DATA / "loot_table" / "blocks" / f"{b}.json", {
        "type": "minecraft:block",
        "pools": [{
            "rolls": 1,
            "entries": [{"type": "minecraft:item", "name": f"{MOD}:{b}"}],
            "conditions": [{"condition": "minecraft:survival_explosion"}],
        }],
    })

write_json(ROOT / "data" / "minecraft" / "tags" / "block" / "mineable" / "pickaxe.json",
           {"values": [f"{MOD}:{b}" for b in BLOCKS]})

# ------------------------------------------------------------------ Sprachdateien

EN = {
    "block.sentinelcraft.generator": "Generator",
    "block.sentinelcraft.wire_lamp": "Wired Lamp",
    "block.sentinelcraft.light_switch": "Light Switch",
    "block.sentinelcraft.keycard_reader": "Keycard Reader",
    "block.sentinelcraft.camera": "Security Camera",
    "item.sentinelcraft.cable_tool": "Cable Tool",
    "item.sentinelcraft.keycard_1": "Keycard (Level 1)",
    "item.sentinelcraft.keycard_2": "Keycard (Level 2)",
    "item.sentinelcraft.keycard_3": "Keycard (Level 3)",
    "message.sentinelcraft.cable_first": "First device selected. Click the device to connect it to.",
    "message.sentinelcraft.cable_cancel": "Selection cancelled.",
    "message.sentinelcraft.cable_far": "Too far apart (max. %s blocks).",
    "message.sentinelcraft.cable_linked": "Cable connected.",
    "message.sentinelcraft.cable_unlinked": "Cable removed.",
    "message.sentinelcraft.cable_full": "Too many cables on one device (max. %s).",
    "message.sentinelcraft.cable_lost": "The first device no longer exists.",
    "message.sentinelcraft.generator_status": "Generator: %s s of fuel left",
    "message.sentinelcraft.generator_full": "The generator is full.",
    "message.sentinelcraft.reader_granted": "Access granted",
    "message.sentinelcraft.reader_denied": "Access denied - level %s keycard required",
    "message.sentinelcraft.reader_level": "Reader now requires level %s",
    "message.sentinelcraft.reader_hint": "Keycard reader (level %s) - hold a keycard and right-click",
    "message.sentinelcraft.not_owner": "Only the owner can change this.",
    "message.sentinelcraft.camera_alert": "Camera at %s, %s, %s spotted %s!",
    "message.sentinelcraft.camera_ignores_owner": "Camera ignores its owner",
    "message.sentinelcraft.camera_detects_all": "Camera detects every player",
}
DE = {
    "block.sentinelcraft.generator": "Generator",
    "block.sentinelcraft.wire_lamp": "Kabellampe",
    "block.sentinelcraft.light_switch": "Lichtschalter",
    "block.sentinelcraft.keycard_reader": "Keycard-Leser",
    "block.sentinelcraft.camera": "Überwachungskamera",
    "item.sentinelcraft.cable_tool": "Kabel-Werkzeug",
    "item.sentinelcraft.keycard_1": "Keycard (Stufe 1)",
    "item.sentinelcraft.keycard_2": "Keycard (Stufe 2)",
    "item.sentinelcraft.keycard_3": "Keycard (Stufe 3)",
    "message.sentinelcraft.cable_first": "Erstes Gerät gewählt. Klicke auf das Gerät, mit dem du es verbinden willst.",
    "message.sentinelcraft.cable_cancel": "Auswahl abgebrochen.",
    "message.sentinelcraft.cable_far": "Zu weit voneinander entfernt (max. %s Blöcke).",
    "message.sentinelcraft.cable_linked": "Kabel verbunden.",
    "message.sentinelcraft.cable_unlinked": "Kabel entfernt.",
    "message.sentinelcraft.cable_full": "Zu viele Kabel an einem Gerät (max. %s).",
    "message.sentinelcraft.cable_lost": "Das erste Gerät existiert nicht mehr.",
    "message.sentinelcraft.generator_status": "Generator: noch %s s Brennstoff",
    "message.sentinelcraft.generator_full": "Der Generator ist voll.",
    "message.sentinelcraft.reader_granted": "Zugriff gewährt",
    "message.sentinelcraft.reader_denied": "Zugriff verweigert – Keycard Stufe %s nötig",
    "message.sentinelcraft.reader_level": "Leser verlangt jetzt Stufe %s",
    "message.sentinelcraft.reader_hint": "Keycard-Leser (Stufe %s) – Keycard in die Hand nehmen und Rechtsklick",
    "message.sentinelcraft.not_owner": "Nur der Besitzer kann das ändern.",
    "message.sentinelcraft.camera_alert": "Kamera bei %s, %s, %s hat %s entdeckt!",
    "message.sentinelcraft.camera_ignores_owner": "Kamera ignoriert ihren Besitzer",
    "message.sentinelcraft.camera_detects_all": "Kamera erkennt jeden Spieler",
}
write_json(ASSETS / "lang" / "en_us.json", EN)
write_json(ASSETS / "lang" / "de_de.json", DE)

print("Assets erzeugt in", ROOT)

import os
from PIL import Image

SRC = r"e:\Persion\claude\Flip Clock\icon.jpg"
RES = r"e:\Persion\claude\Flip Clock\app\src\main\res"

# Density multipliers relative to mdpi (1x)
DENSITIES = {
    "mdpi": 1,
    "hdpi": 1.5,
    "xhdpi": 2,
    "xxhdpi": 3,
    "xxxhdpi": 4,
}

# Legacy icon sizes (dp * density)
LEGACY_SIZES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192,
}

# Adaptive foreground sizes (108dp * density)
FOREGROUND_SIZES = {
    "mdpi": 108,
    "hdpi": 162,
    "xhdpi": 216,
    "xxhdpi": 324,
    "xxxhdpi": 432,
}

BG_COLOR = "#1F1F1F"  # matches the dark border of the icon

img = Image.open(SRC).convert("RGBA")

# --- Legacy icons ---
for density, size in LEGACY_SIZES.items():
    folder = os.path.join(RES, f"mipmap-{density}")
    os.makedirs(folder, exist_ok=True)
    resized = img.resize((size, size), Image.LANCZOS)
    resized.save(os.path.join(folder, "ic_launcher.png"), "PNG")
    print(f"  Legacy {density}: {size}x{size}")

# --- Adaptive icon foreground ---
for density, size in FOREGROUND_SIZES.items():
    folder = os.path.join(RES, f"mipmap-{density}")
    os.makedirs(folder, exist_ok=True)
    resized = img.resize((size, size), Image.LANCZOS)
    resized.save(os.path.join(folder, "ic_launcher_foreground.png"), "PNG")
    print(f"  Foreground {density}: {size}x{size}")

# --- Adaptive icon XML (anydpi-v26) ---
anydpi_folder = os.path.join(RES, "mipmap-anydpi-v26")
os.makedirs(anydpi_folder, exist_ok=True)

for name in ["ic_launcher", "ic_launcher_round"]:
    xml = f"""<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>"""
    with open(os.path.join(anydpi_folder, f"{name}.xml"), "w", encoding="utf-8") as f:
        f.write(xml)
    print(f"  XML: {name}.xml")

# --- Color resource ---
values_folder = os.path.join(RES, "values")
os.makedirs(values_folder, exist_ok=True)
color_xml = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#1F1F1F</color>
</resources>"""
with open(os.path.join(values_folder, "colors.xml"), "w", encoding="utf-8") as f:
    f.write(color_xml)
print("  colors.xml")

print("\nDone! All icon resources generated.")

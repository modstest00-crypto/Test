# App Icon Guide for SnapSort

## Icon Folders

Place your app icons in the following folders:

```
app/src/main/res/
├── mipmap-mdpi/      # 48x48 px icons for mdpi screens
├── mipmap-hdpi/      # 72x72 px icons for hdpi screens
├── mipmap-xhdpi/     # 96x96 px icons for xhdpi screens
├── mipmap-xxhdpi/    # 144x144 px icons for xxhdpi screens
└── mipmap-xxxhdpi/   # 192x192 px icons for xxxhdpi screens
```

## Required Icon Files

For each folder, create these files:

1. **ic_launcher.png** - Main app icon
2. **ic_launcher_round.png** - Round version of app icon
3. **ic_launcher_foreground.png** - Foreground layer (for Android 8.0+)
4. **ic_launcher_background.png** - Background layer (for Android 8.0+)

## Icon Specifications

### Adaptive Icons (Android 8.0+)
- **Size**: 108x108 dp (create at higher resolution for each density)
- **Safe Zone**: Keep important content within center 66x66 dp
- **Background**: Solid color or gradient
- **Foreground**: Your app logo/symbol

### Traditional Icons
- Create square icons with rounded corners
- Follow Material Design iconography guidelines

## Recommended Icon Design for SnapSort

### Colors
- Primary: #7C3AED (Purple)
- Secondary: #14B8A6 (Teal)
- Background: White or gradient

### Design Ideas
1. **Screenshot + Sort**: Show stacked images with sorting arrows
2. **AI Brain + Image**: Neural network pattern over photo
3. **Letter S**: Stylized "S" with screenshot elements
4. **Grid + Sparkle**: Photo grid with magic/AI sparkle

## Online Tools

- [Material Design Icons](https://fonts.google.com/icons)
- [Android Asset Studio](http://romannurik.github.io/AndroidAssetStudio/)
- [App Icon Generator](https://appicon.co/)

## Example ic_launcher.xml (Vector)

If using vector icons, create `ic_launcher.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#7C3AED"
        android:pathData="M0,0h108v108H0z"/>
    <!-- Your icon paths here -->
</vector>
```

## Testing

After adding icons:
1. Clean project: `./gradlew clean`
2. Rebuild: `./gradlew assembleDebug`
3. Install on device and verify icons display correctly

## Current Placeholder

Currently using a simple purple background with foreground vector.
Replace with your custom design for production.

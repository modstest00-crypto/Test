# SnapSort - AI-Powered Screenshot Organizer 🎨

<div align="center">

![SnapSort Logo](app/src/main/res/drawable/ic_launcher_foreground.xml)

**Organize your screenshots intelligently with on-device AI**

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://docs.oracle.com/javase/)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue.svg)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34-purple.svg)](https://developer.android.com/about/versions/14)

</div>

---

## ✨ Features

### 🤖 AI-Powered Classification
- **On-Device Processing**: All AI runs locally using TensorFlow Lite
- **12 Smart Categories**: Automatically categorizes into Social, Chat, Gaming, Shopping, News, Music, Video, Maps, Finance, Productivity, Settings, and More
- **Auto-Albums**: Smart detection for Shopping Lists, Tickets, and To-Do Lists

### 🔍 Natural Language Search
Search your screenshots using everyday language:
- *"Show me the Wi-Fi password from last week"*
- *"Find the receipt over $50"*
- *"Shopping screenshots from yesterday"*
- *"Tickets for my upcoming flight"*

### 🎯 Professional UI/UX
- **Material Design 3**: Modern, beautiful interface
- **Bottom Navigation**: Easy access to all sections
- **Tab Layout**: Quick category filtering
- **Gradient Cards**: Eye-catching stat displays
- **Smooth Animations**: Polished user experience

### 🌍 Country-Specific Pricing
Pro features with localized prices for 40+ countries:
- 🇺🇸 USA: $2.99
- 🇬🇧 UK: £2.49
- 🇪🇺 Europe: €2.99
- 🇮🇳 India: ₹249
- 🇳🇬 Nigeria: ₦4,490
- 🇯🇵 Japan: ¥380
- 🇧🇷 Brazil: R$14.90
- And many more!

### 💎 Pro Features
| Feature | Free | Pro |
|---------|------|-----|
| Basic Categorization | ✅ | ✅ |
| Auto-Albums | ✅ | ✅ |
| Natural Language Search | ✅ | ✅ |
| Cloud Backup (100GB) | ❌ | ✅ |
| Advanced AI Models | ❌ | ✅ |
| Priority Support | ❌ | ✅ |
| Advanced Export (PDF/Video) | ❌ | ✅ |

---

## 📱 Screenshots

### Home Screen
- Stats cards with gradient backgrounds
- Quick action buttons (Scan & Organize)
- Auto-albums horizontal scroll
- Categories grid
- Recent images with selection mode

### Pro Upgrade Screen
- Beautiful gradient header card
- Feature list with icons
- Country selector with localized pricing
- One-time purchase & subscription options

---

## 🏗️ Architecture

```
SnapSort/
├── app/src/main/
│   ├── java/com/snapsort/app/
│   │   ├── adapter/           # RecyclerView Adapters
│   │   │   ├── CategoryAdapter.java
│   │   │   ├── ScreenshotAdapter.java
│   │   │   └── AutoAlbumAdapter.java
│   │   ├── model/             # Room Entities & DAOs
│   │   │   ├── Screenshot.java
│   │   │   ├── Category.java
│   │   │   ├── AutoAlbum.java
│   │   │   ├── CountryPricing.java  ← Country-specific pricing
│   │   │   └── *.java
│   │   ├── util/              # Utilities
│   │   │   ├── ImageClassifier.java   ← TensorFlow Lite
│   │   │   ├── ScreenshotScanner.java
│   │   │   ├── AutoAlbumDetector.java
│   │   │   └── NaturalLanguageSearch.java
│   │   ├── viewmodel/         # ViewModels (MVVM)
│   │   │   └── MainViewModel.java
│   │   ├── MainActivity.java
│   │   ├── ProUpgradeActivity.java  ← Pro purchase screen
│   │   └── *.java
│   ├── res/
│   │   ├── layout/            # XML Layouts
│   │   ├── drawable/          # Vector Drawables
│   │   ├── values/            # Colors, Strings, Themes
│   │   ├── menu/              # Bottom Navigation Menu
│   │   └── mipmap-*/          # App Icons (see ICON_GUIDE.md)
│   ├── assets/
│   │   ├── model.tflite       # TensorFlow Lite Model
│   │   └── labels.txt         # Classification Labels
│   └── AndroidManifest.xml
├── build.gradle
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Installation

1. **Clone/Copy the project**
   ```bash
   cd SnapSort
   ```

2. **Add your app icons** (see [ICON_GUIDE.md](ICON_GUIDE.md))
   ```
   Place icons in: app/src/main/res/mipmap-*/
   ```

3. **Add TensorFlow Lite Model** (Optional)
   - Download MobileNetV2 TFLite model
   - Place in `app/src/main/assets/model.tflite`

4. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Install on device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🎨 Customization

### Change App Icon
See [ICON_GUIDE.md](ICON_GUIDE.md) for detailed instructions.

### Modify Colors
Edit `app/src/main/res/values/colors.xml`:
```xml
<color name="primary">#7C3AED</color>
<color name="secondary">#14B8A6</color>
```

### Add More Countries
Edit `CountryPricing.java`:
```java
PRO_ONE_TIME_PRICES.put("XX", new ProPrice("CUR", "SymbolX.XX", X.XX));
```

---

## 🔐 Privacy

- **100% Offline**: All AI processing happens on your device
- **No Data Collection**: No user data sent to external servers
- **Local Storage**: All data stored locally using Room database
- **Encrypted Cloud**: Optional Pro feature with end-to-end encryption

---

## 💰 Monetization

### Free Features
- Basic screenshot scanning
- AI categorization (12 categories)
- Auto-albums (Shopping Lists, Tickets, To-Do)
- Natural language search
- Local organization

### Pro One-Time Purchase
- **Price**: $2.99 USD (localized per country)
- Encrypted cloud backup (100GB)
- Advanced AI models
- Priority support
- Advanced export options

### Cloud Subscription
- **Price**: $1.99/month USD (localized per country)
- 100GB encrypted storage
- Auto-sync across devices
- Version history (30 days)

---

## 📋 Permissions

| Permission | Usage | Android Version |
|------------|-------|-----------------|
| `READ_EXTERNAL_STORAGE` | Access screenshots | ≤ Android 12 |
| `READ_MEDIA_IMAGES` | Access images | Android 13+ |
| `MANAGE_EXTERNAL_STORAGE` | Organize files | Android 11+ |

---

## 🛠️ Tech Stack

- **Language**: Java
- **UI Framework**: Material Design 3
- **Database**: Room (SQLite)
- **AI/ML**: TensorFlow Lite
- **Architecture**: MVVM
- **Image Loading**: Glide
- **Dependency Injection**: Manual (can add Hilt)

---

## 📝 To-Do

- [ ] OCR integration for text extraction
- [ ] Custom category creation
- [ ] Batch operations
- [ ] Home screen widget
- [ ] Dark mode theme
- [ ] Google Play Billing integration
- [ ] Cloud backup implementation

---

## 📄 License

This project is for educational purposes.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

## 📧 Support

For issues and feature requests, please create an issue in the repository.

---

<div align="center">

**Made with ❤️ using Java & TensorFlow Lite**

[Report Bug](../../issues) · [Request Feature](../../issues)

</div>

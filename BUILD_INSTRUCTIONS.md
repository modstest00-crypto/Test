# 🚀 GitHub Build Guide for SnapSort

## How to Build Your App Using GitHub (Free!)

---

## Option 1: GitHub Actions (Recommended - FREE)

GitHub Actions can automatically build your app every time you push code!

### Step 1: Push Your Code to GitHub

```bash
# Navigate to your project
cd /sdcard/SnapSort

# Initialize git
git init

# Add all files
git add .

# Create first commit
git commit -m "Initial commit - SnapSort app"

# Create repository on GitHub.com (name: SnapSort)
# Then connect and push
git remote add origin https://github.com/YOUR_USERNAME/SnapSort.git
git branch -M main
git push -u origin main
```

### Step 2: GitHub Actions Will Auto-Build

I've created a workflow file that will:
- ✅ Build your app automatically
- ✅ Create debug APK
- ✅ Create release APK
- ✅ Save builds as artifacts (download for 90 days)

---

## Option 2: GitHub + Online Build Services

### A. Using Codemagic (FREE - Recommended for Mobile)

1. **Go to:** https://codemagic.io
2. **Sign in** with GitHub
3. **Add your SnapSort repository**
4. **Click "Start building"**
5. **Download APK** when done!

**No configuration needed!** Codemagic auto-detects Android projects.

### B. Using Bitrise (FREE tier)

1. **Go to:** https://bitrise.io
2. **Sign in** with GitHub
3. **Add SnapSort repository**
4. **Use Android template**
5. **Build and download**

### C. Using AppCenter (FREE - Microsoft)

1. **Go to:** https://appcenter.ms
2. **Sign in** with GitHub
3. **Add new app** → Select SnapSort repo
4. **Configure build**
5. **Auto-builds on every push**

---

## Option 3: Build Locally then Upload to GitHub

### Build on Your Device (Termux)

```bash
cd /sdcard/SnapSort

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK (needs signing)
./gradlew assembleRelease
```

### Upload Build to GitHub Releases

```bash
# Go to your GitHub repo
# Click "Releases" → "Create new release"
# Upload: app/build/outputs/apk/debug/app-debug.apk
```

Users can then download APK directly from GitHub!

---

## 📁 Files Created for GitHub

### 1. `.github/workflows/android-build.yml`
Auto-builds your app on every push!

### 2. `.gitignore`
Excludes build files, keeps repo clean.

### 3. `BUILD_INSTRUCTIONS.md` (this file)
How to build using GitHub.

---

## 🎯 Recommended Workflow

### For Development (Daily Use)

```bash
# Make changes to code
git add .
git commit -m "Fixed bug in billing"
git push origin main

# GitHub Actions builds automatically
# Download APK from Actions tab
```

### For Release (Play Store)

```bash
# Update version in build.gradle
# Commit and tag
git commit -m "Release v1.0"
git tag v1.0.0
git push origin main --tags

# GitHub Actions creates release build
# Download and upload to Play Console
```

---

## 📥 How to Download Built APK

### From GitHub Actions:

1. Go to your repo on GitHub
2. Click **"Actions"** tab
3. Click the latest build
4. Scroll to **"Artifacts"**
5. Click to download APK
6. Extract ZIP (password: `android`)

### From GitHub Releases:

1. Go to your repo
2. Click **"Releases"**
3. Download APK from latest release
4. Install on your phone!

---

## ⚙️ GitHub Actions Build Configuration

The workflow file builds:

| Build Type | Output | Use |
|------------|--------|-----|
| Debug APK | `app-debug.apk` | Testing |
| Release APK | `app-release-unsigned.apk` | Play Store |
| Android App Bundle | `app-release.aab` | Play Store upload |

**Build Time:** ~10-15 minutes

---

## 🔐 Signing Your Release Build

### For Play Store Upload:

1. **Create keystore** (one-time):
```bash
keytool -genkey -v -keystore snapsort.keystore -alias snapsort -keyalg RSA -keysize 2048 -validity 10000
```

2. **Create `keystore.properties`**:
```properties
storePassword=your_password
keyPassword=your_password
keyAlias=snapsort
storeFile=../snapsort.keystore
```

3. **Update `app/build.gradle`** (already configured)

4. **Upload keystore to GitHub Secrets**:
   - Repo Settings → Secrets → Actions
   - Add `KEYSTORE_BASE64` (encoded keystore)
   - Add `KEYSTORE_PASSWORD`
   - Add `KEY_ALIAS`
   - Add `KEY_PASSWORD`

---

## 🆘 Troubleshooting

### Build Fails on GitHub

**Check:**
1. `build.gradle` has correct SDK versions
2. All dependencies are available
3. No local paths in code

**View logs:**
- Go to Actions tab
- Click failed build
- Read error in logs

### "SDK not found" Error

GitHub Actions has Android SDK pre-installed. No action needed!

### "License not accepted" Error

Add this to your workflow (already included):
```yaml
- name: Accept Android licenses
  run: yes | sdkmanager --licenses
```

---

## 📊 Build Status Badge

Add this to your README.md:

```markdown
![Build Status](https://github.com/YOUR_USERNAME/SnapSort/workflows/Android%20Build/badge.svg)
```

Shows if your latest build passed! ✅

---

## 🎯 Quick Start Summary

### First Time Setup (10 minutes):

```bash
# 1. Initialize git
cd /sdcard/SnapSort
git init
git add .
git commit -m "Initial commit"

# 2. Create GitHub repo (github.com/new)
# Name: SnapSort
# Public or Private (your choice)

# 3. Push code
git remote add origin https://github.com/YOUR_USERNAME/SnapSort.git
git branch -M main
git push -u origin main

# 4. Wait for build (10-15 min)
# Go to Actions tab on GitHub
# Download APK from first build
```

### Every Update:

```bash
git add .
git commit -m "Updated feature"
git push

# Auto-builds! Check Actions tab after 10 min
```

---

## 💰 Monetization + GitHub

Your billing code is already included! When you build via GitHub:

1. **BillingManager.java** is included ✅
2. **Product IDs** are set ✅
3. **Country pricing** is included ✅

Just make sure to:
- Create products in Play Console with matching IDs
- Upload signed release build to Play Store
- Money flows to your bank! 💰

---

## 🔗 Helpful Links

| Resource | Link |
|----------|------|
| GitHub Actions | https://github.com/features/actions |
| Codemagic (Android CI) | https://codemagic.io |
| Bitrise (Mobile CI) | https://bitrise.io |
| AppCenter (Microsoft) | https://appcenter.ms |
| Android Gradle Docs | https://developer.android.com/studio/build |

---

## ✅ Checklist

- [ ] Create GitHub account (if you don't have)
- [ ] Create SnapSort repository
- [ ] Push code to GitHub
- [ ] Check Actions tab for auto-build
- [ ] Download first APK
- [ ] Test on your phone
- [ ] Set up automatic builds
- [ ] Create release for Play Store

---

**You're ready to build with GitHub!** 🚀

Questions? Check the workflow file at `.github/workflows/android-build.yml`

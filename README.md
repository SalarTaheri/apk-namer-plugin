# APK Namer Plugin

پلاگین گرادل برای نام‌گذاری خودکار فایل‌های APK بر اساس flavor، versionName و buildType.

## 🚀 راه‌اندازی و انتشار (JitPack)

### ۱. جایگزینی یوزرنیم گیت‌هاب

توی فایل `build.gradle.kts`، هر جا `YOUR_GITHUB_USERNAME` نوشته شده رو با یوزرنیم گیت‌هاب خودت جایگزین کن.

### ۲. اجرایی کردن gradlew (⚠️ مرحله‌ی حیاتی)

JitPack برای build کردن پروژه به فایل‌های **Gradle Wrapper** نیاز داره (این پروژه از قبل شاملشونه: `gradlew`, `gradlew.bat`, `gradle/wrapper/`). بدون این فایل‌ها، JitPack خودش می‌ره با یه Gradle خیلی قدیمی (4.8.1) wrapper می‌سازه که با `kotlin-dsl` و Java 17 سازگار نیست و build شکست می‌خوره.

قبل از commit، حتماً بیت اجرایی رو روی `gradlew` فعال کن (توی لینوکس/مک):

```bash
chmod +x gradlew
```

(اگه ویندوز داری و امکان اجرای این دستور نیست، از WSL یا Git Bash استفاده کن — این بیت باید توی خود گیت ثبت بشه.)

### ۳. ساخت ریپو و پوش کردن

```bash
git init
git add .
git commit -m "Initial plugin release"
git branch -M main
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/apk-namer-plugin.git
git push -u origin main
```

می‌تونی مطمئن بشی بیت اجرایی درست ثبت شده با:

```bash
git ls-files -s gradlew
# باید با 100755 شروع بشه، نه 100644
```

### ۴. تگ زدن (این تگ همون version میشه)

```bash
git tag 1.0.0
git push origin 1.0.0
```

### ۵. (اختیاری ولی پیشنهادی) فعال‌سازی روی JitPack

برو به آدرس زیر و روی "Get it" کلیک کن تا اولین build اجرا بشه و مطمئن شی مشکلی نیست:

```
https://jitpack.io/#YOUR_GITHUB_USERNAME/apk-namer-plugin
```

اگه build سبز شد (Green)، یعنی همه‌چیز آماده‌ست. اگه نه، لاگ build رو همونجا می‌تونی ببینی.

---

## 📦 استفاده در پروژه‌های دیگه

### `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        maven("https://jitpack.io")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "ir.miranmahaleh.salar.apk-namer") {
                useModule("com.github.YOUR_GITHUB_USERNAME:apk-namer-plugin:1.0.0")
            }
        }
    }
}
```

### `build.gradle.kts` ماژول app

```kotlin
plugins {
    id("com.android.application")
    id("ir.miranmahaleh.salar.apk-namer")
}

apkNamer {
    enabled = true
    separator = "_"
}
```

## 🔄 انتشار نسخه‌ی جدید

هر وقت تغییری دادی، کافیه ورژن رو توی `build.gradle.kts` آپدیت کنی و یک تگ جدید بزنی:

```bash
git add .
git commit -m "Update: ..."
git push

git tag 1.1.0
git push origin 1.1.0
```

و در پروژه‌ی مصرف‌کننده فقط ورژن رو توی `useModule(...)` آپدیت کن.

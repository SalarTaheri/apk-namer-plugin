# پلاگین APK Namer (مستندات فارسی)

[🇬🇧 English Documentation](README.md)

[![Release](https://jitpack.io/v/SalarTaheri/apk-namer-plugin.svg)](https://jitpack.io/#SalarTaheri/apk-namer-plugin)

پلاگین قدرتمند و منعطف گرادل برای نام‌گذاری خودکار و شخصی‌سازی نام فایل‌های APK در پروژه‌های اندروید بر اساس نام پروژه، نام پایه دلخواه، Flavor، نوع بیلد، نسخه برنامه، تاریخ، هش گیت و الگوهای سفارشی.

فرمت پیش‌فرض نام‌گذاری خروجی:
```text
<baseName><separator><flavorName><separator><buildType><separator><versionName>.apk
```
**نمونه‌ها:**
- بدون Flavor: `MyApp_release_1.5.0.apk`
- با Flavor: `GatePay_urovoProd_debug_1.0.apk`
- با الگو و تاریخ: `GatePay-urovoProd-debug-v1.0-20260929.apk`

---

## 📦 نحوه استفاده از طریق JitPack

آخرین نسخه منتشر شده: **`1.5.0`**

### روش اول: از طریق `pluginManagement` (روش استاندارد و پیشنهادی)

#### ۱. فایل `settings.gradle.kts` (یا `settings.gradle`)

مخزن JitPack و نگاشت ماژول را در بخش `pluginManagement` اضافه کنید:

**Kotlin DSL (`settings.gradle.kts`):**
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "ir.miranmahaleh.salar.apk-namer") {
                useModule("com.github.SalarTaheri:apk-namer-plugin:1.5.0")
            }
        }
    }
}
```

**Groovy DSL (`settings.gradle`):**
```groovy
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url 'https://jitpack.io' }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == 'ir.miranmahaleh.salar.apk-namer') {
                useModule('com.github.SalarTaheri:apk-namer-plugin:1.5.0')
            }
        }
    }
}
```

#### ۲. فایل `build.gradle.kts` ماژول اپلیکیشن (مثلاً `app/build.gradle.kts`)

پلاگین را در بخش `plugins` فعال کنید:

**Kotlin DSL (`app/build.gradle.kts`):**
```kotlin
plugins {
    alias(libs.plugins.android.application) // یا id("com.android.application")
    id("ir.miranmahaleh.salar.apk-namer")
}
```

---

### روش دوم: از طریق `buildscript classpath` (کلاسیک)

اگر ترجیح می‌دهید از `pluginManagement` استفاده نکنید، می‌توانید مستقیماً در فایل `build.gradle.kts` ریشه (Root Project) پلاگین را اضافه کنید:

**Kotlin DSL (Root `build.gradle.kts`):**
```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.SalarTaheri:apk-namer-plugin:1.5.0")
    }
}
```

سپس در `app/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application")
    id("ir.miranmahaleh.salar.apk-namer")
}
```

---

## ⚙️ تنظیمات و سفارشی‌سازی (Configuration)

می‌توانید رفتار پلاگین را در `build.gradle.kts` ماژول اپلیکیشن از طریق بلاک `apkNamer` شخصی‌سازی کنید:

### نمونه ۱: تنظیم نام پایه، جداکننده و افزودن تاریخ یا کد نسخه

```kotlin
apkNamer {
    enabled = true
    baseName = "GatePay"           // در صورت مشخص نشدن، نام rootProject در نظر گرفته می‌شود
    separator = "_"                // جداکننده بین بخش‌ها
    includeVersionName = true
    includeVersionCode = false
    includeDate = true             // افزودن تاریخ بیلد
    dateFormat = "yyyyMMdd"        // فرمت تاریخ
    includeGitSha = false          // افزودن هش کامیت گیت
}
```

### نمونه ۲: استفاده از الگوی دلخواه (Template Pattern)

می‌توانید با استفاده از توکن‌ها، ترتیب و فرمت نام فایل را دقیقاً به شکل دلخواه بسازید:

```kotlin
apkNamer {
    pattern = "{baseName}-{flavor}-{buildType}-v{versionName}-{date}"
}
```

**توکن‌های در دسترس:**
* `{baseName}`: نام مشخص شده در `baseName` یا نام ریشه پروژه
* `{rootProject}`: نام روت پروژه
* `{moduleName}` / `{project}`: نام ماژول اپ (مانند `app`)
* `{flavor}` یا `{flavorName}`: نام طعم بیلد (مانند `urovoProd`)
* `{buildType}`: نوع بیلد (`debug` یا `release`)
* `{versionName}`: نام نسخه (`1.0.0`)
* `{versionCode}`: کد نسخه عددی
* `{date}` / `{timestamp}`: تاریخ بیلد طبق `dateFormat`
* `{gitSha}`: هش کوتاه آخرین کامیت گیت
* `{variantName}`: نام کامل متغیر بیلد (مانند `urovoProdDebug`)

### نمونه ۳: تغییر استایل حروف (Case Format) و پیشوند/پسوند

```kotlin
apkNamer {
    baseName = "GatePay"
    caseFormat = CaseFormat.KEBAB_CASE // یا SNAKE_CASE, LOWERCASE, UPPERCASE, PRESERVE
    prefix = "Release_"
    suffix = "_signed"
}
```

### نمونه ۴: اعمال فیلتر بر روی بیلدها (Target / Exclude BuildTypes)

```kotlin
apkNamer {
    // تغییر نام فقط برای بیلد‌های release
    targetBuildTypes = listOf("release")
    
    // یا نادیده گرفتن debug:
    // excludeBuildTypes = listOf("debug")
}
```

### نمونه ۵: شخصی‌سازی ۱۰۰٪ با لامبدا (Custom Resolver)

```kotlin
apkNamer {
    outputFileName { ctx ->
        if (ctx.buildType == "debug") {
            "GatePay_Test_${ctx.flavorName}_v${ctx.versionName}.apk"
        } else {
            "GatePay_${ctx.flavorName}_${ctx.versionName}.apk"
        }
    }
}
```

---

### جدول کامل گزینه‌های تنظیمات:

| گزینه | نوع | مقدار پیش‌فرض | توضیحات |
| :--- | :--- | :--- | :--- |
| `enabled` | `Boolean` | `true` | فعال یا غیرفعال کردن کل پلاگین |
| `baseName` | `String?` | `null` | نام پایه APK (پیش‌فرض: `rootProject.name`) |
| `separator` | `String` | `"_"` | کاراکتر یا رشته جداکننده بین اجزای نام APK |
| `pattern` | `String?` | `null` | الگوی رشته‌ای نام فایل با پشتیبانی از توکن‌ها |
| `prefix` | `String` | `""` | پیشوند متنی قبل از نام فایل |
| `suffix` | `String` | `""` | پسوند متنی قبل از `.apk` |
| `includeBaseName` | `Boolean` | `true` | درج نام پایه در حالت پیش‌فرض |
| `includeFlavor` | `Boolean` | `true` | درج Flavor در صورت وجود |
| `includeBuildType` | `Boolean` | `true` | درج نوع بیلد (`release`/`debug`) |
| `includeVersionName`| `Boolean` | `true` | درج شماره نسخه برنامه |
| `includeVersionCode`| `Boolean` | `false` | درج کد نسخه عددی برنامه |
| `includeDate` | `Boolean` | `false` | درج تاریخ ایجاد بیلد |
| `includeGitSha` | `Boolean` | `false` | درج هش کوتاه کامیت Git |
| `dateFormat` | `String` | `"yyyyMMdd"` | فرمت تاریخ بر اساس `SimpleDateFormat` |
| `caseFormat` | `CaseFormat` | `PRESERVE` | استایل تبدیل حروف (`PRESERVE`, `LOWERCASE`, `UPPERCASE`, `SNAKE_CASE`, `KEBAB_CASE`) |
| `targetBuildTypes` | `List<String>`| `emptyList()` | اعمال نام‌گذاری تنها روی بیلدتایپ‌های مشخص |
| `excludeBuildTypes` | `List<String>`| `emptyList()` | مستثنی کردن بیلدتایپ‌های مشخص از تغییر نام |
| `outputFileName(block)` | `(VariantContext) -> String` | `null` | تابع لامبدا برای تعریف منطق کاملاً اختصاصی |

---

## 📋 پیش‌نیازها و سازگاری

- **Java**: 17 یا بالاتر
- **Android Gradle Plugin (AGP)**: نسخه 8.0.0 یا بالاتر
- **Gradle**: نسخه 8.0 یا بالاتر

---

## 🛠 راهنمای توسعه و انتشار نسخه جدید (برای نگه‌دارندگان)

برای انتشار نسخه جدید روی JitPack:

۱. نسخه را در فایل `build.gradle.kts` به‌روزرسانی کنید.
۲. تغییرات را کامیت و پوش کنید:
```bash
git add .
git commit -m "Release version 1.5.0"
git push
```
۳. یک Git Tag ایجاد کرده و پوش نمایید:
```bash
git tag 1.5.0
git push origin 1.5.0
```
۴. وضعیت بیلد نسخه جدید را در صفحه [JitPack SalarTaheri/apk-namer-plugin](https://jitpack.io/#SalarTaheri/apk-namer-plugin) بررسی کنید.

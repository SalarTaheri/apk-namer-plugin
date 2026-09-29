# APK Namer Plugin

[![Release](https://jitpack.io/v/SalarTaheri/apk-namer-plugin.svg)](https://jitpack.io/#SalarTaheri/apk-namer-plugin)

> A modern, flexible Gradle plugin for Android projects to automatically customize and structure APK output filenames based on project names, flavors, build types, versions, timestamps, Git commit hashes, and custom template patterns.

[🇮🇷 مطالعه مستندات به زبان فارسی (Persian Documentation)](README_FA.md)

---

## 🌟 Key Features

- **Custom Base Name**: Override the default `app` or `rootProject.name` with a dedicated app title (e.g., `GatePay`).
- **Template Pattern Engine**: Define naming formats using dynamic tokens (e.g., `"{baseName}-{flavor}-v{versionName}-{date}"`).
- **Rich Dynamic Tokens**: Supports `{baseName}`, `{rootProject}`, `{moduleName}`, `{flavor}`, `{buildType}`, `{versionName}`, `{versionCode}`, `{date}`, `{gitSha}`, and `{variantName}`.
- **Smart Delimiter Sanitization**: Automatically removes consecutive and trailing/leading separators when optional components (such as flavors) are absent.
- **Case Transformations**: Seamlessly format filenames to `SNAKE_CASE`, `KEBAB_CASE`, `LOWERCASE`, `UPPERCASE`, or keep original `PRESERVE`.
- **Build Type Filtering**: Restrict renaming to specific variants (e.g., only `release` builds).
- **Custom Closure / Lambda**: Total programmatic control via Kotlin lambda when complex naming rules are required.
- **AGP 8.x+ Ready**: Built natively for Android Gradle Plugin 8.x and Gradle 8+.

---

## 📐 Default Naming Scheme

When no custom pattern is supplied, the plugin structures the filename sequentially:

```text
<baseName><separator><flavorName><separator><buildType><separator><versionName>.apk
```

**Examples:**
- Without Flavor: `MyApp_release_1.5.0.apk`
- With Flavor: `GatePay_urovoProd_debug_1.0.apk`
- With Pattern & Timestamp: `GatePay-urovoProd-debug-v1.0-20260929.apk`

---

## 📦 Installation via JitPack

Latest Release: **`1.5.0`**

### Option 1: Modern `pluginManagement` (Recommended)

#### 1. In `settings.gradle.kts` (or `settings.gradle`)

Add the JitPack repository and plugin resolution mapping:

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

#### 2. In your Application module (`app/build.gradle.kts`)

Apply the plugin in the `plugins` block:

**Kotlin DSL (`app/build.gradle.kts`):**
```kotlin
plugins {
    alias(libs.plugins.android.application) // or id("com.android.application")
    id("ir.miranmahaleh.salar.apk-namer")
}
```

**Groovy DSL (`app/build.gradle`):**
```groovy
plugins {
    id 'com.android.application'
    id 'ir.miranmahaleh.salar.apk-namer'
}
```

---

### Option 2: Classic `buildscript classpath`

If you prefer applying plugins via the root project's buildscript:

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

Then in `app/build.gradle.kts`:
```kotlin
plugins {
    id("com.android.application")
    id("ir.miranmahaleh.salar.apk-namer")
}
```

---

## ⚙️ Configuration & Examples

Configure the plugin in your application module's `build.gradle.kts` via the `apkNamer` extension block:

### Example 1: Custom Base Name with Date and Git SHA

```kotlin
apkNamer {
    baseName = "GatePay"           // Defaults to rootProject.name if omitted
    separator = "_"                // Delimiter between components
    includeVersionName = true
    includeVersionCode = false
    includeDate = true             // Append build date
    dateFormat = "yyyyMMdd"        // Date format string (SimpleDateFormat)
    includeGitSha = true           // Append short Git commit hash
}
```

### Example 2: Flexible Template Pattern

Tokens allow custom ordering and arbitrary separators:

```kotlin
apkNamer {
    baseName = "GatePay"
    pattern = "{baseName}-{flavor}-{buildType}-v{versionName}-{date}"
}
```

**Available Tokens:**
| Token | Description | Example |
| :--- | :--- | :--- |
| `{baseName}` | Specified base name or root project name | `GatePay` |
| `{rootProject}` | Name of the root Gradle project | `GatePayProject` |
| `{moduleName}` / `{project}` | Name of the current Android module | `app` |
| `{flavor}` / `{flavorName}` | Product flavor name | `urovoProd` |
| `{buildType}` | Build type name | `release` / `debug` |
| `{versionName}` | Application version name | `1.0.0` |
| `{versionCode}` | Application integer version code | `12` |
| `{date}` / `{timestamp}` | Formatted build date | `20260929` |
| `{gitSha}` | Short Git commit hash | `f3a1b02` |
| `{variantName}` | Full variant identifier | `urovoProdDebug` |

### Example 3: Case Transformation, Prefix & Suffix

```kotlin
apkNamer {
    baseName = "GatePay"
    caseFormat = CaseFormat.KEBAB_CASE // PRESERVE, LOWERCASE, UPPERCASE, SNAKE_CASE, KEBAB_CASE
    prefix = "CI_"
    suffix = "_signed"
}
```

### Example 4: Build Type Filtering

```kotlin
apkNamer {
    // Only rename APKs for release builds
    targetBuildTypes = listOf("release")

    // Or exclude specific build types:
    // excludeBuildTypes = listOf("debug")
}
```

### Example 5: 100% Custom Resolver Lambda

For advanced custom requirements, define a Kotlin closure with access to `VariantContext`:

```kotlin
apkNamer {
    outputFileName { ctx ->
        if (ctx.buildType == "debug") {
            "GatePay_Test_${ctx.flavorName}_v${ctx.versionName}.apk"
        } else {
            "GatePay_${ctx.flavorName}_${ctx.versionName}_${ctx.date}.apk"
        }
    }
}
```

---

## 📖 Configuration Reference

| Option | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `enabled` | `Boolean` | `true` | Enables or disables automated APK renaming. |
| `baseName` | `String?` | `null` | Base application name. Falls back to `rootProject.name` if unset. |
| `separator` | `String` | `"_"` | Separator delimiter between default components. |
| `pattern` | `String?` | `null` | Custom string template with token interpolation. |
| `prefix` | `String` | `""` | String prepended to the final filename. |
| `suffix` | `String` | `""` | String appended before the `.apk` extension. |
| `includeBaseName` | `Boolean` | `true` | Include base name in default scheme. |
| `includeFlavor` | `Boolean` | `true` | Include product flavor if available. |
| `includeBuildType` | `Boolean` | `true` | Include build type (`release`/`debug`). |
| `includeVersionName` | `Boolean` | `true` | Include application version name. |
| `includeVersionCode` | `Boolean` | `false` | Include application version code. |
| `includeDate` | `Boolean` | `false` | Include build date. |
| `includeGitSha` | `Boolean` | `false` | Include short Git commit SHA. |
| `dateFormat` | `String` | `"yyyyMMdd"` | Date pattern used for `{date}` and `includeDate`. |
| `caseFormat` | `CaseFormat` | `PRESERVE` | Character case conversion (`PRESERVE`, `LOWERCASE`, `UPPERCASE`, `SNAKE_CASE`, `KEBAB_CASE`). |
| `targetBuildTypes` | `List<String>` | `emptyList()` | List of build types to process. If empty, all are processed. |
| `excludeBuildTypes` | `List<String>` | `emptyList()` | List of build types to ignore. |
| `outputFileName(block)` | `(VariantContext) -> String` | `null` | Custom lambda resolver for absolute control. |

---

## 📋 Compatibility

- **Java**: 17 or higher
- **Android Gradle Plugin (AGP)**: 8.0.0 or higher
- **Gradle**: 8.0 or higher

---

## 🛠 Maintainers Guide (Publishing to JitPack)

To release a new version to JitPack:

1. Update the version string in `build.gradle.kts`.
2. Commit and push the changes:
   ```bash
   git add .
   git commit -m "Release version 1.5.0"
   git push
   ```
3. Create and push a Git tag:
   ```bash
   git tag 1.5.0
   git push origin 1.5.0
   ```
4. Check the build status on [JitPack SalarTaheri/apk-namer-plugin](https://jitpack.io/#SalarTaheri/apk-namer-plugin).

---

## 📄 License

This project is licensed under the Apache 2.0 License.

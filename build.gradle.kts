plugins {
    `kotlin-dsl`
    `maven-publish`
}

// ⚠️ YOUR_GITHUB_USERNAME رو با یوزرنیم گیت‌هاب خودت جایگزین کن
group = "com.github.YOUR_GITHUB_USERNAME"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // ورژن AGP رو با پروژه‌های مصرف‌کننده هماهنگ نگه دار
    compileOnly("com.android.tools.build:gradle:8.5.2")
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("apkNamer") {
            id = "ir.miranmahaleh.salar.apk-namer"
            implementationClass = "ApkNamerPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.YOUR_GITHUB_USERNAME"
            artifactId = "apk-namer-plugin"
            version = "1.0.0"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

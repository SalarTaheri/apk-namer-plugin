plugins {
    `kotlin-dsl`
    `maven-publish`
}

// JitPack Group & Version
group = "com.github.SalarTaheri"
version = "1.5.0"

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
            groupId = "com.github.SalarTaheri"
            artifactId = "apk-namer-plugin"
            version = "1.5.0"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    // Generates (de)serialization code for `@Serializable` DTOs (Data layer)
    alias(libs.plugins.kotlinSerialization)
    // Generates type-safe DAOs and Entities from `.sq` files (local source)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            // Native Android HTTP engine for Ktor
            implementation(libs.ktor.client.android)
            // Koin Android helpers (Application context, etc.)
            implementation(libs.koin.android)
            // Native Android SQLite driver for SQLDelight
            implementation(libs.sqldelight.android.driver)
        }
        commonMain.dependencies {
            // --- Compose Multiplatform (shared UI) ---
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // --- Navigation 3 (type-safe centralized Navigator) ---
            implementation(libs.navigation3.ui)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)

            // --- Ktor client (Data layer: remote source) ---
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // --- JSON serialization for DTOs ---
            implementation(libs.kotlinx.serialization.json)

            // --- Koin (shared DI) ---
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // --- SQLDelight (local source: DAO/Entity pair) ---
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            // Java HTTP engine for Ktor on Desktop
            implementation(libs.ktor.client.java)
            // JVM SQLite driver for SQLDelight on Desktop
            implementation(libs.sqldelight.sqlite.driver)
        }
    }
}

android {
    namespace = "fr.leandremru.rickandmortylocations"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "fr.leandremru.rickandmortylocations"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "fr.leandremru.rickandmortylocations.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "fr.leandremru.rickandmortylocations"
            packageVersion = "1.0.0"
        }
    }
}

// SQLDelight configuration: declares the database that will be generated
// from `.sq` files placed under `composeApp/src/commonMain/sqldelight/`
sqldelight {
    databases {
        create("LocationsDatabase") {
            packageName.set("fr.leandremru.rickandmortylocations.data.local.db")
        }
    }
}

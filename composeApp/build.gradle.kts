import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)                    
    alias(libs.plugins.room)                   
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // iOS targets — precisa de 3 porque iOS tem simulador ARM64 e device ARM64
    listOf(
        iosX64(),           // simulador Intel (Macs antigos)
        iosArm64(),         // device real
        iosSimulatorArm64() // simulador Apple Silicon (M1/M2/M3)
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    // SOURCE SETS: onde o código de cada plataforma vive
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.iconsExtended)

            // Lifecycle (ViewModel KMP)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime)

            // Navegação Compose KMP
            implementation(libs.compose.navigation)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Room — dependência comum
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // DataStore
            implementation(libs.datastore.preferences)

            // Kotlinx
            implementation(libs.uuid)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)

            // Coil (imagens)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.androidx.core.splashscreen)
        }

        // androidMain: código que SÓ roda no Android 
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.ui.tooling.preview)
            implementation(libs.koin.android)

            // Ktor client Android
            implementation(libs.ktor.client.okhttp)
        }

        // iosMain: código que SÓ roda no iOS 
        iosMain.dependencies {
            // Ktor client iOS
            implementation(libs.ktor.client.darwin)
        }

        // commonTest: testes que rodam em todas as plataformas 
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "br.com.moapetapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "br.com.moapetapp"
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

// ROOM + KSP: configuração para geração de código

// Configura o Room para exportar o schema (útil para migrations futuras)
room {
    schemaDirectory("$projectDir/schemas")
}

// KSP precisa saber para qual plataforma gerar o código do Room.
// No KMP, geramos para Android + iOS (simulador e device).
dependencies {
    debugImplementation(libs.compose.uiTooling)
    debugImplementation(libs.androidx.ui.tooling)

    // Gera o código Room para Android
    add("kspAndroid", libs.room.compiler)

    // Gera o código Room para iOS (simuladores e device real)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

import org.gradle.api.GradleException
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.compose")
}


android {
    namespace = "com.example.vetclinic"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vetclinic"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }

    buildTypes {

        debug {
            buildConfigField("String", "SUPABASE_URL", getSupabaseUrl())
            buildConfigField("String", "SUPABASE_KEY", getSupabaseKey())
        }


        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SUPABASE_URL", getSupabaseUrl())
            buildConfigField("String", "SUPABASE_KEY", getSupabaseKey())
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.android)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor.v4100)

    implementation(libs.assisted.inject.annotations.dagger2)
    kapt(libs.assisted.inject.processor.dagger2)

    implementation(libs.glide)
    implementation (libs.androidx.viewpager2)
    implementation (libs.androidx.swiperefreshlayout)
    implementation(libs.material.v130alpha03)
    implementation (libs.materialdatetimepicker)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.supabase.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.realtime.kt)
    implementation(libs.storage.kt)
    implementation(libs.functions.kt)
    implementation(libs.auth.kt)

    implementation (libs.androidx.paging.runtime)
    implementation (libs.androidx.room.paging)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.viewModel)
    implementation(libs.room)
    implementation(libs.room.ktx)

    ksp(libs.room.compiler)
    implementation(libs.dagger2)
    ksp(libs.dagger2.compiler)
    ksp(libs.dagger2.android.processor)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockito.core)
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation (libs.mockk.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)

    implementation ("com.jakewharton.timber:timber:5.0.1")

    // Compose core
    implementation("androidx.compose.ui:ui:1.8.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.8.2")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.2")

// Compose Material 3
    implementation("androidx.compose.material3:material3:1.3.2")

// Compose Material
    implementation("androidx.compose.material:material:1.8.2")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

// Pull refresh
    implementation("androidx.compose.foundation:foundation:1.8.2")
    implementation("androidx.compose.foundation:foundation-layout:1.8.2")

// Paging
    implementation("androidx.paging:paging-compose:3.3.6")

// Activity + Lifecycle
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.8.2")

    implementation("androidx.navigation:navigation-compose:2.7.7")

}

fun getProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
        return properties
    } else {
        throw GradleException("local.properties not found!")
    }
}

fun getSupabaseUrl(): String {
    return "\"${getProperties().getProperty("SUPABASE_URL")}\""
}

fun getSupabaseKey(): String {
    return "\"${getProperties().getProperty("SUPABASE_KEY")}\""
}



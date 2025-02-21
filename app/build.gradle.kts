import org.gradle.api.GradleException
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")

    id("com.google.gms.google-services")
    id ("kotlin-kapt")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.firebase.bom)

    implementation("io.ktor:ktor-client-core:3.0.0")
    implementation("io.ktor:ktor-client-okhttp:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")

    implementation("io.ktor:ktor-client-android:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")  // OkHttp core
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")


    // Glide для загрузки изображений
    implementation("com.github.bumptech.glide:glide:4.16.0")




    implementation(platform(libs.supabase.bom))
    implementation(libs.postgrest.kt)
    implementation(libs.realtime.kt)
    implementation(libs.storage.kt)
    implementation(libs.functions.kt)

    implementation(libs.retrofit)


    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.logging.interceptor)


    implementation(libs.viewModel)
    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.dagger2)
    ksp(libs.dagger2.compiler)
    ksp(libs.dagger2.android.processor)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
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


// Function to read properties from local.properties

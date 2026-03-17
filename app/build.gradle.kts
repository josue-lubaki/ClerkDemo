import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ca.josuelubaki.ui.clerkdemo"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "ca.josuelubaki.ui.clerkdemo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        val apiKey: String = localProperties.getProperty("CLERK_PUBLISHABLE_KEY")
        val supabaseUrl: String = localProperties.getProperty("SUPABASE_URL")
        val supabaseKey: String = localProperties.getProperty("SUPABASE_KEY")

        require(apiKey.isNotEmpty()) {
            "API_KEY manquante dans local.properties"
        }

        require(supabaseUrl.isNotEmpty()) {
            "SUPABASE_URL manquante dans local.properties"
        }

        require(supabaseKey.isNotEmpty()) {
            "SUPABASE_KEY manquante dans local.properties"
        }

        buildConfigField(
            "String",
            "CLERK_PUBLISHABLE_KEY",
            "\"$apiKey\""
        )

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"$supabaseUrl\""
        )
        buildConfigField(
            "String",
            "SUPABASE_KEY",
            "\"$supabaseKey\""
        )


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // clerk
    implementation("com.clerk:clerk-android-api:1.0.10")
    implementation("com.clerk:clerk-android-ui:1.0.10")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    // supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.4.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
}
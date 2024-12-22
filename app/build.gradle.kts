plugins {
    id("kotlin-android")
    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.meinerezepte"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.meinerezepte"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.config.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation(platform("com.google.firebase:firebase-config:21.1.1"))
    implementation(platform("com.google.firebase:firebase-analytics:21.2.0"))

}
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("com.google.gms:google-services:4.3.15")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
    dependencies {
        // Firebase dependencies

    }
}



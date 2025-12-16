// app/build.gradle.kts

plugins {
    id("com.android.application")

    // !!! Применение плагина Google Services !!!
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.theatreapp" // Замените на namespace вашего проекта
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.theatreapp" // Замените на ID вашего приложения
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    // !!! Включение поддержки Java 8 !!!
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // --- Основные AndroidX зависимости ---
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- Зависимости для работы с фрагментами и ViewModel/LiveData ---
    // Используем обычные (не KTX) версии, так как вы пишете на Java
    implementation("androidx.fragment:fragment:1.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")

    // --- Firebase Platform (BOM - Bill of Materials) ---
    // BOM позволяет не указывать версии для каждой отдельной библиотеки Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // --- Firebase Authentication (Для логина) ---
    // Используем обычную (не KTX) версию для Java
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // --- Firebase Realtime Database (Для хранения данных) ---
    // Используем обычную (не KTX) версию для Java
    implementation("com.google.firebase:firebase-database")

    // --- Тестовые зависимости (необязательно) ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.core:core-ktx:1.12.0")
}
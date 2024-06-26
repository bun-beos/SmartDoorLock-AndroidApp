plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "vn.edu.hust.ttkien0311.smartlockdoor"
    compileSdk = 34

    defaultConfig {
        applicationId = "vn.edu.hust.ttkien0311.smartlockdoor"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Moshi
    implementation ("com.squareup.moshi:moshi:1.13.0")
    implementation ("com.squareup.moshi:moshi-kotlin:1.13.0")
    // Coil
    implementation ("io.coil-kt:coil:2.5.0")

    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation ("androidx.security:security-crypto:1.0.0")

    // Image picker
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    // Swipe refresh layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    // RecyclerViewSwipeDecorator
    implementation ("it.xabaras.android:recyclerview-swipedecorator:1.4")
}
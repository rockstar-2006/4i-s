plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.rider"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rider"
        minSdk = 26
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:21.0.8")
    implementation("com.airbnb.android:lottie:5.0.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.navigation:navigation-fragment:2.8.0")
    implementation("androidx.navigation:navigation-ui:2.8.0")
    implementation("androidx.wear:wear:1.3.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("com.sothree.slidinguppanel:library:3.4.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    implementation ("com.google.android.libraries.places:places:3.2.0")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation ("com.google.maps.android:android-maps-utils:2.2.5")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// For Volley
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.maps.android:android-maps-utils:2.2.5")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("androidx.navigation:navigation-fragment:2.6.0")
    implementation ("androidx.navigation:navigation-ui:2.6.0")




}
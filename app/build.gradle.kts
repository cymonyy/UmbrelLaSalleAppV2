plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.mobdeve.s15.nadela.oliva.quizon.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mobdeve.s15.nadela.oliva.quizon.myapplication"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets{
        getByName("main"){
            res.srcDirs(
                "src/main/res/layouts/components",
                "src/main/res/layouts",
                "src/main/res"

            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }






}

dependencies {

//    val appcompatVersion = "1.6.1"
//    implementation("androidx.core:core-ktx:1.12.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.10.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("androidx.recyclerview:recyclerview:1.3.2")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    implementation("androidx.appcompat:appcompat-resources:$appcompatVersion")
//
//    // Import the Firebase BoM
//    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
//
//
//
//    // When using the BoM, don't specify versions in Firebase dependencies
//    implementation("com.google.firebase:firebase-analytics-ktx")
//
//
//    // Add the dependencies for any other desired Firebase products
//    // https://firebase.google.com/docs/android/setup#available-libraries
//    implementation("com.google.firebase:firebase-database")
//    implementation("com.google.firebase:firebase-firestore:24.9.1")
//    implementation("com.google.firebase:firebase-auth:22.3.0")
//    implementation("com.google.firebase:firebase-analytics:21.5.0")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.camera:camera-view:1.3.0")
    val appcompatVersion = "1.6.1"

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.lifecycle:lifecycle-common:2.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.appcompat:appcompat-resources:$appcompatVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // CameraX dependencies
    implementation ("androidx.camera:camera-core:1.4.0-alpha02")
    implementation ("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation ("androidx.camera:camera-lifecycle:1.4.0-alpha02")

    //ML Kit Barcode Scanning
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")

    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))


    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics-ktx")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")

    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")


    val fragmentVersion = "1.6.2"

    // Java language implementation
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    // Testing Fragments in Isolation
    debugImplementation("androidx.fragment:fragment-testing:$fragmentVersion")


}
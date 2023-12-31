plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.kekulta.androidband"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kekulta.androidband"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    }
}

dependencies {
    /**
     * Room is an relational database and ORM from Google
     */
    implementation("androidx.room:room-runtime:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")

    /**
     * LinearLayout that wrapping its children
     */
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    /**
     * ViewPager2 is a google's lib for easy for swipeviews
     */
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    /**
     * Koin is a lightweight DI(or ServiceLocator) framework
     * It could have some perfomance issues with big projects but fine with small ones
     */
    implementation("io.insert-koin:koin-android:3.5.0")

    /**
     *  FFmpeg is a owerful tool for Audio processing
     */
    implementation("com.arthenica:mobile-ffmpeg-audio:4.4")

    /**
     * Activity's and Fragment's extensions
     */
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    /**
     * Coroutines core
     */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    /**
     * Coroutines support in Android
     */
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    /**
     * ViewBinding delegate for easy bindings
     */
    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.9")

    /**
     * Standard Android stuff
     */
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    /**
     * Test stuff
     */
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
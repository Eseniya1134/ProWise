plugins {
    //id("com.android.application")
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ourpro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ourpro"
        minSdk = 23
        targetSdk = 35
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.firebase:firebase-auth:23.2.0")
    implementation ("androidx.credentials:credentials:1.5.0-rc01")
    implementation ("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-database:21.0.0")
    implementation ("com.google.firebase:firebase-firestore:25.1.4")

    implementation ("com.squareup.picasso:picasso:2.8")//предназначена для загрузки и отображения изображений в Android-приложениях.
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01") //поддерживает вертикальный свайп, анимации, RTL и RecyclerView внутри.
    implementation ("com.google.android.material:material:1.10.0") // рисует сами вкладки (TabLayout) и другие красивые элементы.

    implementation ("com.cloudinary:cloudinary-android:2.5.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0") // Для загрузки файлов

    implementation ("de.hdodenhof:circleimageview:3.1.0") //библиотека для округления картинок
    implementation ("org.mindrot:jbcrypt:0.4") //бд supabase


    implementation ("com.github.bumptech.glide:glide:4.15.1") //библиотека для загрузки гифок, роликов, картинок
}

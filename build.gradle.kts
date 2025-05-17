// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google() // Добавляем репозиторий Google
        mavenCentral() // Добавляем репозиторий Maven Central
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        // Добавляем Google Services plugin
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
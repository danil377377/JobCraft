// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("convention.detekt")
    id("androidx.room") version "2.6.1" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.24"
}

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.github.ben-manes.versions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.frankmassi.posturereminder"
        minSdkVersion(28)
        targetSdkVersion(28)
        versionCode = 6
        versionName = "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = project.extra["buildToolsVersion"].toString()

    dataBinding {
        isEnabled = true
    }
    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", project.extra["kotlinVersion"].toString()))
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.work:work-runtime-ktx:${project.extra["workVersion"]}")
    implementation("com.google.android.material:material:1.0.0")
    implementation("com.android.support:recyclerview-v7:${project.extra["supportVersion"]}")
    implementation("com.android.support:design:${project.extra["supportVersion"]}")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.work:work-testing:${project.extra["workVersion"]}")


    // Room components
    implementation("androidx.room:room-runtime:${project.extra["roomVersion"]}")
    implementation("androidx.room:room-ktx:${project.extra["roomVersion"]}")
    kapt("androidx.room:room-compiler:${project.extra["roomVersion"]}")
    androidTestImplementation("androidx.room:room-testing:${project.extra["roomVersion"]}")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:${project.extra["archLifecycleVersion"]}")
    kapt("androidx.lifecycle:lifecycle-compiler:${project.extra["archLifecycleVersion"]}")
    androidTestImplementation("androidx.arch.core:core-testing:${project.extra["androidxArchVersion"]}")

    // ViewModel Kotlin support
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${project.extra["archLifecycleVersion"]}")

    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.extra["coroutines"]}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:${project.extra["coroutines"]}")
}

tasks.withType < KotlinCompile > {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("tagGithub", Exec::class) {
    workingDir("../bin")
    executable("python3")
    args("tag_release.py", android.defaultConfig.versionName + "-" + android.defaultConfig.versionCode)
}


fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,\\.v\\-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType < DependencyUpdatesTask > {
    rejectVersionIf {
        isNonStable(candidate.version)
    }

    checkForGradleUpdate = true
    revision = "release"
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

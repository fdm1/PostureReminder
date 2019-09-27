import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.github.ben-manes.versions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.frankmassi.posturereminder"
        minSdkVersion(28)
        targetSdkVersion(28)
        versionCode = 5
        versionName ="0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled=false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildToolsVersion = "28.0.3"


}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", project.rootProject.extra["kotlinVersion"].toString()))
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.work:work-runtime-ktx:${project.rootProject.extra["workVersion"]}")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.work:work-testing:${project.rootProject.extra["workVersion"]}")
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("tagGithub", Exec::class) {
    workingDir( "../bin")
    executable("python3")
    args ("tag_release.py", android.defaultConfig.versionName + "-" + android.defaultConfig.versionCode)
}

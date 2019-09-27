import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.0")
        classpath(kotlin("gradle-plugin", version = "1.3.50"))
        classpath("com.github.ben-manes:gradle-versions-plugin:0.25.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }

    extra.apply {
        set("workVersion", "2.2.0")
        set("kotlinVersion", "1.3.50")
        set("buildToolsVersion", "28.0.3")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}


tasks.withType<DependencyUpdatesTask> {
    // optional parameters
    checkForGradleUpdate = true
    revision = "release"
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

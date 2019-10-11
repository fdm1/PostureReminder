import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.1")
        classpath(kotlin("gradle-plugin", version = "1.3.50"))
        classpath("com.github.ben-manes:gradle-versions-plugin:0.26.0")

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
        set("roomVersion", "2.2.0")
        set("archLifecycleVersion", "2.1.0")
        set("androidxArchVersion", "2.1.0")
        set("coroutines", "1.3.2")
        set("supportVersion", "28.0.0")
    }
}

subprojects {
    ext.apply {
    }

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}


plugins {
    java
    id("xyz.jpenilla.run-paper") version "3.0.1"
}

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.9-R0.1-SNAPSHOT")
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks {
    runServer {
        minecraftVersion("1.21.9")
        systemProperty("Paper.IgnoreJavaVersion", "true")
        systemProperty("LetMeReload", "true")
        jvmArgs("-Xmx4G", "-Xms4G")

//        jvmArgs("-Xmx4G", "-Xms4G",
//            "-XX:+AllowEnhancedClassRedefinition",
//
//            "-XX:ReservedCodeCacheSize=1G",
//            "-XX:NonProfiledCodeHeapSize=256M",
//            "-XX:ProfiledCodeHeapSize=256M"
//        )

//        jvmArgs(
//            "-Xms4G",
//            "-Xmx4G",
//
//            "-XX:+AlwaysPreTouch",
//            "-XX:+DisableExplicitGC",
//            "-XX:+ParallelRefProcEnabled",
//            "-XX:+PerfDisableSharedMem",
//            "-XX:+UnlockExperimentalVMOptions",
//            "-XX:+UseG1GC",
//            "-XX:G1HeapRegionSize=16M",
//            "-XX:G1HeapWastePercent=5",
//            "-XX:G1MaxNewSizePercent=40",
//            "-XX:G1MixedGCCountTarget=4",
//            "-XX:G1MixedGCLiveThresholdPercent=90",
//            "-XX:G1NewSizePercent=30",
//            "-XX:G1RSetUpdatingPauseTimePercent=5",
//            "-XX:G1ReservePercent=20",
//            "-XX:InitiatingHeapOccupancyPercent=15",
//            "-XX:MaxGCPauseMillis=200",
//            "-XX:MaxTenuringThreshold=1",
//            "-XX:SurvivorRatio=32",
//
//            "-XX:ReservedCodeCacheSize=2G",
//            "-XX:NonProfiledCodeHeapSize=1G",
//            "-XX:ProfiledCodeHeapSize=1000M",
//
//            "-XX:+UseCodeCacheFlushing",
//            "-XX:+SegmentedCodeCache",
//
//            "-XX:+UseStringDeduplication",
//            "-XX:+UseFastUnorderedTimeStamps",
//            "-XX:+OptimizeStringConcat",
//
//            "-Dusing.aikars.flags=https://mcflags.emc.gs",
//            "-Daikars.new.flags=true"
//        )
    }
}
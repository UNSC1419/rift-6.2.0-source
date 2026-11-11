import org.jetbrains.compose.reload.gradle.ComposeHotRun
import org.jetbrains.compose.reload.gradle.isHotReloadBuild
import org.koin.compiler.plugin.KoinGradleExtension
import java.time.Instant

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.hotreload)
    alias(libs.plugins.spotless)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.koin.compiler) apply false
    id("dev.hydraulic.conveyor") version "1.12"
}

val environment = (properties["rift.environment"] as? String) ?: "dev"
val isMacOs = System.getProperty("os.name").startsWith("Mac", ignoreCase = true)
val isLinux = System.getProperty("os.name").startsWith("Linux", ignoreCase = true)

if (!isHotReloadBuild) {
    pluginManager.apply("io.insert-koin.compiler.plugin")
    if (environment == "dev") {
        extensions.configure<KoinGradleExtension>("koinCompiler") {
            // The compiler plugin is required by Koin's generated DSL calls, but
            // strict safety disables incremental compilation for the whole module.
            strictSafety.set(false)
            strictSafetyForceOff.set(true)
        }
    }
}

val riftVersion = properties["rift.version"] as String
group = "dev.nohus"
version = riftVersion

buildConfig {
    buildConfigField("String", "environment", "\"$environment\"")
    buildConfigField("Boolean", "isDevEnvironment", "${environment == "dev"}")
    buildConfigField("String", "version", "\"${properties["rift.version"]}\"")
    val buildTimestamp = if (environment == "dev") 0L else Instant.now().toEpochMilli()
    buildConfigField("long", "buildTimestamp", "$buildTimestamp")
    buildConfigField("String", "buildUuid", "\"${properties["rift.buildUuid"]}\"")
    buildConfigField("String", "sentryDsn", "${properties["rift.sentryDsn"]}")
    buildConfigField("String", "postHogToken", "${properties["rift.postHogToken"]}")
    buildConfigField("String", "focusedLoggers", "${properties["rift.focusedLoggers"]}")
    buildConfigField("String", "logLevel", "${properties["rift.logLevel"]}")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jogamp.org/deployment/maven")
    google()
}

dependencies {
    // Compose
    implementation(libs.compose.desktop.linux)
    implementation(libs.compose.desktop.macos.x64)
    implementation(libs.compose.desktop.macos.arm64)
    implementation(libs.compose.desktop.windows)
    implementation(libs.compose.components.resources)
    implementation(libs.compose.desktop) {
        exclude("org.jetbrains.compose.material")
        exclude("org.jetbrains.compose.material3")
    }

    // Kamel
    implementation(libs.kamel.image)
    implementation(libs.kamel.decoder.image.bitmap)
    implementation(libs.kamel.decoder.animatedimage)
    implementation(libs.kamel.fetcher.resources)

    // Logging
    implementation(libs.kotlin.logging)
    implementation(libs.logback.classic)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.koin.annotations)

    // Other
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.commons.lang3)
    implementation(libs.commons.exec)
    implementation(libs.commons.math3)
    implementation(libs.commons.csv)
    implementation(libs.httpclient5)
    implementation(libs.jose4j)
    implementation(libs.flatlaf)
    implementation(libs.jsoup)
    implementation(libs.autolink)
    implementation(libs.jts.core)
    implementation(libs.haze)
    implementation(libs.haze.blur)
    implementation(libs.conveyor.control)
    implementation(libs.androidx.collection)
    implementation(libs.jbr.api)
    implementation(libs.cache4k)
    implementation(libs.snakeyaml.engine)

    // OpenAL Audio
    implementation(libs.joal.main)
    implementation(libs.gluegen.rtmain)

    implementation(libs.jlayer)

    // JavaCV / OpenCV
    implementation(libs.opencv.platform)

    // Smack (XMPP)
    implementation(libs.smack.java8)
    implementation(libs.smack.tcp)
    implementation(libs.smack.im)
    implementation(libs.smack.extensions)

    // JNA
    implementation(libs.jna)
    implementation(libs.jna.platform)

    // System tray
    implementation(libs.compose.native.tray)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)

    // Ktor
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinxjson)

    // Sentry
    implementation(platform(libs.sentry.bom))
    implementation(libs.sentry)
    implementation(libs.sentry.logback)

    implementation(libs.posthog)

    // Testing
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
}

compose.desktop {
    application {
        mainClass = "dev.nohus.rift.MainKt"
        jvmArgs(
            "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.lwawt=ALL-UNNAMED",
            "--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED",
        )
        if (isMacOs) {
            jvmArgs("--add-opens=java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }
        if (isLinux) {
            jvmArgs("--add-opens=java.desktop/sun.awt.X11=ALL-UNNAMED")
            jvmArgs("-Djoal.openal.lib=system")
        }

        nativeDistributions {
            modules("java.sql", "java.naming", "jdk.naming.dns")
        }
    }
}

tasks.withType<ComposeHotRun>().configureEach {
    mainClass.set("dev.nohus.rift.MainKt")
    jvmArgs(
        "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
        "--add-opens=java.desktop/sun.lwawt=ALL-UNNAMED",
        "--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED",
        "--enable-native-access=ALL-UNNAMED"
    )
    if (isMacOs) {
        jvmArgs("--add-opens=java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
    }
    if (isLinux) {
        jvmArgs("--add-opens=java.desktop/sun.awt.X11=ALL-UNNAMED")
        jvmArgs("-Djoal.openal.lib=system")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
        optIn.add("kotlin.ExperimentalStdlibApi")
        optIn.add("kotlin.ExperimentalVersionOverloading")
        optIn.add("kotlin.contracts.ExperimentalContracts")
        optIn.add("org.jetbrains.compose.resources.ExperimentalResourceApi")
        freeCompilerArgs.add("-Xallow-returns-result-of")
        freeCompilerArgs.add("-Xcollection-literals")
        freeCompilerArgs.add("-Xexplicit-context-arguments")
        freeCompilerArgs.add("-Xintrinsic-const-evaluation")
        freeCompilerArgs.add("-Xreturn-value-checker=check")
        freeCompilerArgs.add("-Xname-based-destructuring=only-syntax")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.JETBRAINS
        implementation = JvmImplementation.VENDOR_SPECIFIC
    }
}

configurations.all {
    if (isCanBeResolved || isCanBeConsumed) {
        attributes {
            attribute(Attribute.of("ui", String::class.java), "awt")
        }
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(mapOf(
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                "ktlint_standard_no-empty-first-line-in-class-body" to "disabled",
                "ktlint_standard_function-expression-body" to "disabled",
                "ktlint_standard_class-signature" to "disabled",
            ))
        targetExclude("**/generated/**")
    }
}

package dev.nohus.rift.di

import org.koin.core.Koin
import org.koin.logger.SLF4JLogger
import org.koin.plugin.module.dsl.startKoin

lateinit var koin: Koin

val koinOrNull: Koin? get() = if (::koin.isInitialized) koin else null

fun startKoin() {
    koin = startKoin<RiftApp> {
        logger(SLF4JLogger())
        modules(factoryModule, platformModule)
    }.koin
}

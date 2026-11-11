package dev.nohus.rift.di

import dev.nohus.rift.logging.analytics.Analytics
import dev.nohus.rift.network.interceptors.CacheOverrideInterceptor
import dev.nohus.rift.network.interceptors.EsiAppHeadersInterceptor
import dev.nohus.rift.network.interceptors.EsiAuthorizationErrorInterceptor
import dev.nohus.rift.network.interceptors.EsiAuthorizationInterceptor
import dev.nohus.rift.network.interceptors.EsiCompatibilityInterceptor
import dev.nohus.rift.network.interceptors.EsiErrorLimitInterceptor
import dev.nohus.rift.network.interceptors.EsiRateLimitInterceptor
import dev.nohus.rift.network.interceptors.LoggingInterceptor
import dev.nohus.rift.network.interceptors.RequestIdInterceptor
import dev.nohus.rift.network.interceptors.UserAgentInterceptor
import dev.nohus.rift.network.requests.CacheStatisticsLocalInterceptor
import dev.nohus.rift.network.requests.CacheStatisticsNetworkInterceptor
import dev.nohus.rift.network.requests.Endpoint
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.network.requests.OriginatorRateLimitInterceptor
import dev.nohus.rift.network.requests.RequestExecutor
import dev.nohus.rift.network.requests.RequestExecutorImpl
import dev.nohus.rift.network.requests.RequestStatisticsInterceptor
import dev.nohus.rift.network.zkillboard.ZkillboardCacheOverrideInterceptor
import dev.nohus.rift.network.zkillboard.ZkillboardRateLimitInterceptor
import dev.nohus.rift.notifications.system.LinuxSendNotificationUseCase
import dev.nohus.rift.notifications.system.MacSendNotificationUseCase
import dev.nohus.rift.notifications.system.SendNotificationUseCase
import dev.nohus.rift.notifications.system.WindowsSendNotificationUseCase
import dev.nohus.rift.utils.GetOperatingSystemUseCase
import dev.nohus.rift.utils.OperatingSystem
import dev.nohus.rift.utils.OperatingSystem.Linux
import dev.nohus.rift.utils.OperatingSystem.MacOs
import dev.nohus.rift.utils.OperatingSystem.Windows
import dev.nohus.rift.utils.directories.AppDirectories
import dev.nohus.rift.utils.evewindows.GetEveWindowsUseCase
import dev.nohus.rift.utils.evewindows.linux.LinuxGetEveWindowsUseCase
import dev.nohus.rift.utils.evewindows.macos.MacGetEveWindowsUseCase
import dev.nohus.rift.utils.evewindows.windows.WindowsGetEveWindowsUseCase
import dev.nohus.rift.utils.osdirectories.LinuxDirectories
import dev.nohus.rift.utils.osdirectories.MacDirectories
import dev.nohus.rift.utils.osdirectories.OperatingSystemDirectories
import dev.nohus.rift.utils.osdirectories.WindowsDirectories
import io.kamel.core.config.Core
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.animatedImageDecoder
import io.kamel.image.config.imageBitmapDecoder
import io.kamel.image.config.resourcesFetcher
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.utils.CacheControl
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.core.qualifier.named
import org.koin.dsl.module

@KoinApplication(modules = [KoinModule::class])
class RiftApp

@Module
@ComponentScan("dev.nohus.rift")
class KoinModule {

    @Single
    @Named("api")
    fun apiOkHttpClient(
        appDirectories: AppDirectories,
        userAgentInterceptor: UserAgentInterceptor,
        requestStatisticsInterceptor: RequestStatisticsInterceptor,
        loggingInterceptor: LoggingInterceptor,
    ): OkHttpClient {
        val directory = appDirectories.getAppCacheDirectory().resolve("http-cache")
        val size = 50L * 1024 * 1024 // 50MB
        return OkHttpClient.Builder()
            .cache(Cache(directory.toFile(), size))
            .addNetworkInterceptor(userAgentInterceptor)
            .addNetworkInterceptor(requestStatisticsInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    @Named("esi")
    fun esiOkHttpClient(
        appDirectories: AppDirectories,
        requestIdInterceptor: RequestIdInterceptor,
        cacheStatisticsLocalInterceptor: CacheStatisticsLocalInterceptor,
        esiCompatibilityInterceptor: EsiCompatibilityInterceptor,
        esiAuthorizationErrorInterceptor: EsiAuthorizationErrorInterceptor,
        esiAuthorizationInterceptor: EsiAuthorizationInterceptor,
        cacheStatisticsNetworkInterceptor: CacheStatisticsNetworkInterceptor,
        esiErrorLimitInterceptor: EsiErrorLimitInterceptor,
        esiRateLimitInterceptor: EsiRateLimitInterceptor,
        originatorRateLimitInterceptor: OriginatorRateLimitInterceptor,
        cacheOverrideInterceptor: CacheOverrideInterceptor,
        userAgentInterceptor: UserAgentInterceptor,
        esiAppHeadersInterceptor: EsiAppHeadersInterceptor,
        requestStatisticsInterceptor: RequestStatisticsInterceptor,
        loggingInterceptor: LoggingInterceptor,
    ): OkHttpClient {
        val dispatcher = Dispatcher().apply {
            maxRequests = 64
            maxRequestsPerHost = 64
        }
        val directory = appDirectories.getAppCacheDirectory().resolve("esi-cache")
        val size = 100L * 1024 * 1024 // 100MB
        return OkHttpClient.Builder()
            .cache(Cache(directory.toFile(), size))
            .dispatcher(dispatcher)
            .addInterceptor(requestIdInterceptor)
            .addInterceptor(cacheStatisticsLocalInterceptor)
            .addInterceptor(esiCompatibilityInterceptor)
            .addInterceptor(esiAuthorizationErrorInterceptor)
            .addNetworkInterceptor(cacheStatisticsNetworkInterceptor)
            .addNetworkInterceptor(esiErrorLimitInterceptor)
            .addNetworkInterceptor(esiRateLimitInterceptor)
            .addNetworkInterceptor(originatorRateLimitInterceptor)
            .addNetworkInterceptor(cacheOverrideInterceptor)
            .addNetworkInterceptor(userAgentInterceptor)
            .addNetworkInterceptor(esiAppHeadersInterceptor)
            .addNetworkInterceptor(esiAuthorizationInterceptor)
            .addNetworkInterceptor(requestStatisticsInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    @Named("zkillr2z2")
    fun zkillboardR2Z2OkHttpClient(
        @Named("zkillCache") cache: Cache,
        userAgentInterceptor: UserAgentInterceptor,
        requestStatisticsInterceptor: RequestStatisticsInterceptor,
        loggingInterceptor: LoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(userAgentInterceptor)
            .addNetworkInterceptor(requestStatisticsInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    @Named("zkill")
    fun zkillboardOkHttpClient(
        @Named("zkillCache") cache: Cache,
        userAgentInterceptor: UserAgentInterceptor,
        zkillboardRateLimitInterceptor: ZkillboardRateLimitInterceptor,
        zkillboardCacheOverrideInterceptor: ZkillboardCacheOverrideInterceptor,
        requestStatisticsInterceptor: RequestStatisticsInterceptor,
        loggingInterceptor: LoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(userAgentInterceptor)
            .addNetworkInterceptor(zkillboardRateLimitInterceptor)
            .addNetworkInterceptor(zkillboardCacheOverrideInterceptor)
            .addNetworkInterceptor(requestStatisticsInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

    @Single
    @Named("zkillCache")
    fun zkillCache(appDirectories: AppDirectories): Cache {
        val directory = appDirectories.getAppCacheDirectory().resolve("zkill-cache")
        val size = 50L * 1024 * 1024 // 50MB
        return Cache(directory.toFile(), size)
    }

    @Single
    @Named("network")
    fun networkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Single
    @Named("settings")
    fun settingsJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
}

val platformModule = module {
    single<OperatingSystem> {
        get<GetOperatingSystemUseCase>()()
    }
    single<OperatingSystemDirectories> {
        when (get<OperatingSystem>()) {
            Linux -> LinuxDirectories()
            Windows -> WindowsDirectories(get())
            MacOs -> MacDirectories()
        }
    }
    single<GetEveWindowsUseCase> {
        when (get<OperatingSystem>()) {
            Linux -> LinuxGetEveWindowsUseCase(get(), get(), get(), get())
            Windows -> WindowsGetEveWindowsUseCase(get(), get())
            MacOs -> MacGetEveWindowsUseCase(get(), get())
        }
    }
    single<SendNotificationUseCase> {
        when (get<OperatingSystem>()) {
            Linux -> LinuxSendNotificationUseCase(get(), get())
            Windows -> WindowsSendNotificationUseCase(get())
            MacOs -> MacSendNotificationUseCase(get())
        }
    }
}

val factoryModule = module {
    factory<RequestExecutor> { RequestExecutorImpl(get(named("network"))) }
    single<Analytics> { Analytics() }
    single<KamelConfig> { getKamelConfig(get(), get()) }
}

private fun getKamelConfig(
    userAgentInterceptor: UserAgentInterceptor,
    requestStatisticsInterceptor: RequestStatisticsInterceptor,
): KamelConfig {
    val logStatisticsPlugin = createClientPlugin("LogStatistics") {
        onResponse {
            requestStatisticsInterceptor.addExternalRequest(Originator.UiImage, Endpoint.ImageServiceAsset, it.status.isSuccess())
        }
    }
    return KamelConfig {
        takeFrom(KamelConfig.Core)
        httpUrlFetcher {
            httpCache(100 * 1024 * 1024)
            defaultRequest {
                header(HttpHeaders.UserAgent, userAgentInterceptor.userAgentValue)
                header(HttpHeaders.CacheControl, CacheControl.MAX_AGE)
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryIf { _, httpResponse ->
                    !httpResponse.status.isSuccess()
                }
            }
            install(logStatisticsPlugin)
        }
        resourcesFetcher()
        imageBitmapDecoder()
        animatedImageDecoder()
        imageBitmapCacheSize = 1000
    }
}

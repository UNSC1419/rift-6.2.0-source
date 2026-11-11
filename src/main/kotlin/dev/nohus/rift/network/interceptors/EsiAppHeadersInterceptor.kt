package dev.nohus.rift.network.interceptors

import dev.nohus.rift.BuildConfig
import dev.nohus.rift.network.requests.Originator
import dev.nohus.rift.utils.GetInstallationIdUseCase
import dev.nohus.rift.utils.OperatingSystem
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class EsiAppHeadersInterceptor(
    operatingSystem: OperatingSystem,
    getInstallationIdUseCase: GetInstallationIdUseCase,
) : Interceptor {

    companion object {
        private const val OPERATING_SYSTEM_KEY = "App-Operating-System"
        private const val FEATURE_KEY = "App-Feature"
        private const val NAME_KEY = "App-Name"
        private const val VERSION_KEY = "App-Version"
        private const val CONTACT_KEY = "App-Contact"
        private const val REQUEST_ID_KEY = "App-Request-Id"
        private const val INSTALLATION_ID_KEY = "App-Installation-Id"
    }

    private val operatingSystemValue = when (operatingSystem) {
        OperatingSystem.Linux -> "Linux"
        OperatingSystem.Windows -> "Windows"
        OperatingSystem.MacOs -> "macOS"
    }
    private val nameValue = "RIFT"
    private val versionValue = "${BuildConfig.version}-source"
    private val contactValue = "developer@riftforeve.online; discord:nohus"
    private val installationId = getInstallationIdUseCase()

    fun getHeaders(feature: String, requestId: UUID = UUID.randomUUID()): Map<String, String> = mapOf(
        NAME_KEY to nameValue,
        VERSION_KEY to versionValue,
        CONTACT_KEY to contactValue,
        OPERATING_SYSTEM_KEY to operatingSystemValue,
        FEATURE_KEY to feature,
        REQUEST_ID_KEY to requestId.toString(),
        INSTALLATION_ID_KEY to installationId,
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originator = checkNotNull(request.tag(Originator::class.java)) { "Missing originator tag" }
        val requestId = checkNotNull(request.tag(UUID::class.java)) { "Missing requestId tag" }

        val newRequest = request.newBuilder().apply {
            getHeaders(originator.name, requestId).forEach { header(it.key, it.value) }
        }.build()
        return chain.proceed(newRequest)
    }
}

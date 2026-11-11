package dev.nohus.rift.notifications.system

import dev.nohus.rift.generated.resources.Res
import dev.nohus.rift.generated.resources.notifications_windows_tray_tooltip
import dev.nohus.rift.localization.AppLocale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.SystemTray
import java.awt.TrayIcon
import javax.imageio.ImageIO
import kotlin.time.Duration.Companion.seconds

class WindowsSendNotificationUseCase(
    private val appLocale: AppLocale,
) : SendNotificationUseCase {

    private val scope = CoroutineScope(Job())

    override fun invoke(appName: String, iconPath: String, summary: String, body: String, timeout: Int) {
        scope.launch {
            val tray = SystemTray.getSystemTray()
            val image = ImageIO.read(Res.readBytes("drawable/tray_tray-16.png").inputStream())
            val trayIcon = TrayIcon(image, appLocale.getString(Res.string.notifications_windows_tray_tooltip))
            trayIcon.isImageAutoSize = true
            tray.add(trayIcon)
            trayIcon.displayMessage(summary, body, TrayIcon.MessageType.NONE)
            delay(7.seconds)
            tray.remove(trayIcon)
        }
    }
}

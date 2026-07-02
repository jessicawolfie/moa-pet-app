package br.com.moapetapp.core.notification

import kotlinx.datetime.Instant

actual fun provideNotificationScheduler(): NotificationScheduler = IosNotificationScheduler()

private class IosNotificationScheduler : NotificationScheduler {
    override suspend fun requestPermission(): Boolean {
        // Implementação básica para iOS pode ser adicionada depois
        return true
    }

    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerArt: Instant
    ) {
        // TODO: Implementar usando UNUserNotificationCenter
    }

    override fun cancelNotification(id: String) {
        // TODO: Implementar usando UNUserNotificationCenter
    }
}

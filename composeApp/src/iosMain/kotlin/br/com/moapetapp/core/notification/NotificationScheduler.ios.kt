package br.com.moapetapp.core.notification

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDateComponents
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

actual fun provideNotificationScheduler(): NotificationScheduler = IosNotificationScheduler()

private class IosNotificationScheduler : NotificationScheduler {
    // O centro de notificações do iOS - ponto único de agendamento/permissão
    private val center = UNUserNotificationCenter.currentNotificationCenter()

    override suspend fun requestPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        // requestAuthorization devolve o resultado num callback
        // suspendCancellableCoroutine "pausa" a função até o callback chamar continuation.resume
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or
                    UNAuthorizationOptionSound or
                    UNAuthorizationOptionBadge,
        ) { granted, _ ->
            continuation.resume(granted)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant,
    ) {
        val granted = requestPermission()
        if (!granted) return

        // Conteúdo da notificação
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
        }

        // Instant (UTC) -> data/hora local -> componentes que o iOS entende
        val localDateTime = triggerAt.toLocalDateTime(TimeZone.currentSystemDefault())
        val components = NSDateComponents().apply {
            year = localDateTime.year.toLong()
            month = localDateTime.monthNumber.toLong()
            day = localDateTime.dayOfMonth.toLong()
            hour = localDateTime.hour.toLong()
            minute = localDateTime.minute.toLong()
        }

        // Trigger de calendário: dispara quando a data/hora bate
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = components,
            repeats = false,
        )

        // Request com o mesmo id do domínio - permite cancelar/sobrescrever depois
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = id,
            content = content,
            trigger = trigger,
        )

        center.addNotificationRequest(request, withCompletionHandler = null)
    }

    override fun cancelNotification(id: String) {
        // Remove o agendamento pendente com esse id. No-op se não existir
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id))
    }
}

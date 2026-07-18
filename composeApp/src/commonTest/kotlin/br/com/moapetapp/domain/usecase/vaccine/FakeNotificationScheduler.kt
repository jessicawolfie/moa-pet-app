package br.com.moapetapp.domain.usecase.vaccine

import br.com.moapetapp.core.notification.NotificationScheduler
import kotlinx.datetime.Instant

/**
 * Fake do NotificationScheduler para testes
 * Não notifica nada, apenas registra as chamadas, pra o teste inspecionar depois
 */
class FakeNotificationScheduler : NotificationScheduler {

    // Guarda cada chamada de scheduleNotification
    data class ScheduleCall(
        val id: String,
        val title: String,
        val body: String,
        val triggerAt: Instant,
    )

    val scheduledCall = mutableListOf<ScheduleCall>() // o que foi agendado
    val cancelledIds = mutableListOf<String>()        // o que foi cancelado

    override suspend fun requestPermission(): Boolean = true

    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant
    ) {
        scheduledCall.add(ScheduleCall(id, title, body, triggerAt))
    }

    override fun cancelNotification(id: String) {
        cancelledIds.add(id)
    }
}